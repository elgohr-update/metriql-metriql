package com.metriql.report.flow

import com.metriql.model.DimensionName
import com.metriql.report.Dataset
import com.metriql.report.funnel.FunnelReportOptions
import com.metriql.warehouse.spi.services.RecipeQuery
import com.metriql.warehouse.spi.services.ServiceReportOptions

data class FlowReportOptions(
    val isStartingEvent: Boolean,
    val event: Dataset,
    val events: List<Dataset>,
    val connector: DimensionName?,
    val stepCount: Int,
    val window: FunnelReportOptions.FunnelWindow?
) : ServiceReportOptions {
    override fun toRecipeQuery(): RecipeQuery {
        TODO("not implemented")
    }
}
