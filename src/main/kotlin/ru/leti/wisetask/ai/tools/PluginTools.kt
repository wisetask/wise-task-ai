package ru.leti.wisetask.ai.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import ru.leti.wise.task.pagination.Pagination
import ru.leti.wise.task.pagination.Pagination.PaginationRequest
import ru.leti.wise.task.plugin.PluginGrpc
import ru.leti.wise.task.plugin.PluginGrpc.GetAllPluginRequest
import ru.leti.wise.task.plugin.PluginServiceGrpc.PluginServiceBlockingStub

@Service
class PluginTools(
    private val pluginService: PluginServiceBlockingStub
) {


    @Tool(description = "Получение списка плагинов")
    fun getPluginNames(): List<String> {
        val getAllPluginRequest = GetAllPluginRequest.newBuilder()
            .setFilter(
                PluginGrpc.PluginFilter.newBuilder()
                    .setIsValid(true)
            ).setPagination(
                PaginationRequest.newBuilder()
                    .setPage(0)
                    .setPageSize(100)
            ).build()
        val pluginsResponse = pluginService.getAllPlugins(
            getAllPluginRequest
        )
        return pluginsResponse.itemsList.map { it.name }
    }

}
