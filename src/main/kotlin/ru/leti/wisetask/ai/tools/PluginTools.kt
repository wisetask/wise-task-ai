package ru.leti.wisetask.ai.tools

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import ru.leti.wise.task.graph.GraphGrpc
import ru.leti.wise.task.graph.GraphOuterClass
import ru.leti.wise.task.graph.GraphServiceGrpc.GraphServiceBlockingStub
import ru.leti.wise.task.pagination.Pagination.PaginationRequest
import ru.leti.wise.task.plugin.PluginGrpc
import ru.leti.wise.task.plugin.PluginGrpc.GetAllPluginRequest
import ru.leti.wise.task.plugin.PluginOuterClass
import ru.leti.wise.task.plugin.PluginServiceGrpc.PluginServiceBlockingStub
import ru.leti.wisetask.ai.utils.SecurityUtils

private val log = KotlinLogging.logger {}

@Service
class PluginTools(
    private val pluginService: PluginServiceBlockingStub,
    private val graphService: GraphServiceBlockingStub
) {
    companion object {
        const val PLUGIN_SIZE = 30
    }

    @Tool(description = "Получение списка проверенных плагинов")
    fun getPluginNames(): List<PluginInfo> {
        log.info{"Request for getting plugins"}
        val getAllPluginRequest = GetAllPluginRequest.newBuilder()
            .setFilter(
                PluginGrpc.PluginFilter.newBuilder()
                    .setIsValid(true)
            ).setPagination(
                PaginationRequest.newBuilder()
                    .setPage(0)
                    .setPageSize(PLUGIN_SIZE)
            ).build()
        val pluginsResponse = pluginService.getAllPlugins(
            getAllPluginRequest
        )
        val plugins =  pluginsResponse.itemsList.map { PluginInfo(
            it.name,
            it.description,
            it.id
        ) }
        log.info { "got some plugins $plugins" }
        return plugins
    }

    @Tool(description = "Получить подробную информацию о плагине: описание, категория, тип")
    fun getPluginDetails(
        @ToolParam(description = "id плагина") pluginId: String
    ): PluginDetails {
        val request = PluginGrpc.GetPluginRequest.newBuilder()
            .setId(pluginId)
            .build()
        val plugin = pluginService.getPlugin(request).plugin
        return PluginDetails(
            pluginId = plugin.id,
            name = plugin.name,
            description = plugin.description,
            category = plugin.category,
            pluginType = plugin.pluginType.name
        )
    }

    @Tool(description = "Выполнить плагин на графе из библиотеки и вернуть результат вычисления")
    fun runPluginOnGraph(
        @ToolParam(description = "id плагина") pluginId: String,
        @ToolParam(description = "id графа") graphId: String
    ): String {
        val graph = graphService.getGraphById(
            GraphGrpc.GetGraphByIdRequest.newBuilder()
                .setId(graphId)
                .build()
        ).graph
        return runPlugin(pluginId, graph)
    }

    @Tool(description = "Сгенерировать случайный граф по параметрам и выполнить на нём плагин")
    fun runPluginOnGeneratedGraph(
        @ToolParam(description = "id плагина") pluginId: String,
        @ToolParam(description = "количество вершин") vertexCount: Int,
        @ToolParam(description = "количество рёбер") edgeCount: Int,
        @ToolParam(description = "ориентированный ли граф") isDirect: Boolean
    ): String {
        val graph = graphService.generateRandomGraph(
            GraphGrpc.GenerateGraphRequest.newBuilder()
                .setVertexCount(vertexCount)
                .setEdgeCount(edgeCount)
                .setIsDirect(isDirect)
                .setIsSaved(false)
                .setAuthorId(SecurityUtils.getUserIdOrAnonymous())
                .build()
        ).graph
        return runPlugin(pluginId, graph)
    }

    private fun runPlugin(pluginId: String, graph: GraphOuterClass.Graph): String {
        val plugin = pluginService.getPlugin(
            PluginGrpc.GetPluginRequest.newBuilder()
                .setId(pluginId)
                .build()
        ).plugin
        val solution = PluginOuterClass.Solution.newBuilder()
            .setPluginId(plugin.id)
            .setPluginType(plugin.pluginType)
            .setGraph(graph)
            .build()
        return pluginService.checkPluginSolution(
            PluginGrpc.CheckPluginSolutionRequest.newBuilder()
                .setSolution(solution)
                .build()
        ).result
    }
}

data class PluginInfo(
    val pluginName: String,
    val pluginDescription: String,
    val pluginId: String
)

data class PluginDetails(
    val pluginId: String,
    val name: String,
    val description: String,
    val category: String,
    val pluginType: String
)
