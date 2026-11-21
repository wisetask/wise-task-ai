package ru.leti.wisetask.ai.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import ru.leti.wise.task.event.Statistic
import ru.leti.wise.task.event.StatisticsServiceGrpc.StatisticsServiceBlockingStub
import ru.leti.wisetask.ai.utils.SecurityUtils

@Service
class StatisticsTools(
    private val statisticsService: StatisticsServiceBlockingStub
) {
    companion object {
        const val SUCCESS_RATE_EVENTS = "task_success,task_wrong"
    }

    @Tool(description = "Получить долю успешных решений задач пользователем, значение от 0 до 1")
    fun getUserSuccessRate(): Double {
        val userId = SecurityUtils.getUserId()
        return getStatistic(
            type = Statistic.StatisticType.SUCCESS_RATE,
            scope = Statistic.StatisticScope.USER,
            eventType = SUCCESS_RATE_EVENTS,
            userId = userId
        )
    }

    @Tool(description = "Получить долю успешных решений задачи всеми пользователями, значение от 0 до 1")
    fun getTaskSuccessRate(
        @ToolParam(description = "id задачи") taskId: String
    ): Double {
        return getStatistic(
            type = Statistic.StatisticType.SUCCESS_RATE,
            scope = Statistic.StatisticScope.TASK,
            eventType = SUCCESS_RATE_EVENTS,
            taskId = taskId
        )
    }

    @Tool(description = "Получить суммарное количество событий пользователя. Типы событий: submit, open_task, task_success, task_wrong, node_add, node_remove, node_color, node_value, edge_add, edge_remove")
    fun getUserEventSum(
        @ToolParam(description = "тип события, например task_success или node_add") eventType: String,
    ): Double {
        val userId = SecurityUtils.getUserId()
        return getStatistic(
            type = Statistic.StatisticType.SUM,
            scope = Statistic.StatisticScope.USER,
            eventType = eventType,
            userId = userId
        )
    }

    private fun getStatistic(
        type: Statistic.StatisticType,
        scope: Statistic.StatisticScope,
        eventType: String,
        taskId: String? = null,
        userId: String? = null
    ): Double {
        val builder = Statistic.StatisticRequest.newBuilder()
            .setType(type)
            .setScope(scope)
            .setEventType(eventType)
        if (scope == Statistic.StatisticScope.USER) {
            builder.setUserId(userId ?: SecurityUtils.getUserId())
        }
        if (taskId != null) {
            builder.setTaskId(taskId)
        }
        return statisticsService.getStatistic(builder.build()).value
    }
}
