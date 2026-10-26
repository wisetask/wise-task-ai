package ru.leti.wisetask.ai.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import ru.leti.wise.task.pagination.Pagination
import ru.leti.wise.task.task.TaskGrpc
import ru.leti.wise.task.task.TaskOuterClass
import ru.leti.wise.task.task.TaskServiceGrpc.TaskServiceBlockingStub

@Service
class TaskTools(
    private val taskService: TaskServiceBlockingStub
) {
    companion object {
        const val TASK_PAGE_SIZE = 20
    }

    @Tool(description = "Поиск задач по названию (частичное совпадение, без учёта регистра). Без запроса возвращает список задач каталога")
    fun searchTasks(
        @ToolParam(description = "поисковый запрос по названию задачи", required = false) name: String? = null,
        @ToolParam(description = "тип задачи: GRAPH или IMPLEMENTATION", required = false) taskType: String? = null,
        @ToolParam(description = "номер страницы, начиная с 0") page: Int = 0
    ): List<TaskSummary> {
        val filter = TaskGrpc.TaskFilter.newBuilder()
        if (name != null) {
            filter.setName(name)
        }
        if (taskType != null) {
            filter.setTaskType(TaskOuterClass.TaskType.valueOf(taskType))
        }
        val request = TaskGrpc.GetAllTaskRequest.newBuilder()
            .setPagination(
                Pagination.PaginationRequest.newBuilder()
                    .setPage(page)
                    .setPageSize(TASK_PAGE_SIZE)
            )
            .setFilter(filter)
            .build()
        return taskService.getAllTask(request).itemsList.map {
            TaskSummary(it.id, it.name, it.category, it.taskType.name)
        }
    }

    @Tool(description = "Получить подробную информацию о задаче: описание, условия и правила. Для задачи типа GRAPH условия это плагины с ожидаемыми значениями, для IMPLEMENTATION это плагин, проверяющий код")
    fun getTaskDetails(
        @ToolParam(description = "id задачи") taskId: String
    ): TaskDetails {
        val request = TaskGrpc.GetTaskRequest.newBuilder()
            .setId(taskId)
            .build()
        val task = taskService.getTask(request).task
        val details = TaskDetails(
            taskId = task.id,
            name = task.name,
            description = task.description,
            category = task.category,
            taskType = task.taskType.name
        )
        return when (task.taskDetailCase) {
            TaskOuterClass.Task.TaskDetailCase.TASK_GRAPH -> {
                details.copy(
                    conditions = task.taskGraph.conditionList.map {
                        TaskCondition(it.pluginId, it.value, it.sign, it.mistakeText)
                    }
                )
            }
            TaskOuterClass.Task.TaskDetailCase.TASK_IMPLEMENTATION -> {
                details.copy(checkingPluginId = task.taskImplementation.pluginId)
            }
            else -> details
        }
    }
}

data class TaskSummary(
    val taskId: String,
    val name: String,
    val category: String,
    val taskType: String
)

data class TaskDetails(
    val taskId: String,
    val name: String,
    val description: String,
    val category: String,
    val taskType: String,
    val conditions: List<TaskCondition> = emptyList(),
    val checkingPluginId: String? = null
)

data class TaskCondition(
    val pluginId: String,
    val expectedValue: String,
    val sign: String,
    val mistakeText: String
)
