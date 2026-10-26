package ru.leti.wisetask.ai.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import ru.leti.wise.task.graph.GraphGrpc
import ru.leti.wise.task.graph.GraphOuterClass
import ru.leti.wise.task.graph.GraphServiceGrpc.GraphServiceBlockingStub
import ru.leti.wisetask.ai.utils.SecurityUtils

@Service
class GraphTools(
    private val graphService: GraphServiceBlockingStub
) {

    @Tool(description = "Получить информацию о графе по его id: количество вершин и рёбер, ориентированность, список рёбер")
    fun getGraphInfo(
        @ToolParam(description = "id графа") graphId: String
    ): GraphInfo {
        val request = GraphGrpc.GetGraphByIdRequest.newBuilder()
            .setId(graphId)
            .build()
        return graphService.getGraphById(request).graph.toGraphInfo()
    }

    @Tool(description = "Сгенерировать случайный граф с заданным количеством вершин и рёбер")
    fun generateRandomGraph(
        @ToolParam(description = "количество вершин") vertexCount: Int,
        @ToolParam(description = "количество рёбер") edgeCount: Int,
        @ToolParam(description = "ориентированный ли граф") isDirect: Boolean,
        @ToolParam(description = "сохранить ли граф в библиотеку текущего пользователя, по умолчанию false", required = false) save: Boolean = false
    ): GraphInfo {
        val request = GraphGrpc.GenerateGraphRequest.newBuilder()
            .setVertexCount(vertexCount)
            .setEdgeCount(edgeCount)
            .setIsDirect(isDirect)
            .setIsSaved(save)
            .setAuthorId(SecurityUtils.getUserIdOrAnonymous())
            .build()
        return graphService.generateRandomGraph(request).graph.toGraphInfo()
    }
}

private const val MAX_EDGES = 50

data class GraphInfo(
    val graphId: String,
    val vertexCount: Int,
    val edgeCount: Int,
    val isDirect: Boolean,
    val edges: List<String>
)

fun GraphOuterClass.Graph.toGraphInfo(): GraphInfo {
    return GraphInfo(
        graphId = id,
        vertexCount = vertexCount,
        edgeCount = edgeCount,
        isDirect = isDirect,
        edges = edgeListList.take(MAX_EDGES).map { "${it.source}->${it.target}" }
    )
}
