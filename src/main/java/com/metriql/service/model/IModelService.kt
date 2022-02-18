package com.metriql.service.model

import com.metriql.service.auth.ProjectAuth

interface IModelService {
    fun list(auth: ProjectAuth, target : Model.Target? = null): List<Model>
    fun getModel(auth: ProjectAuth, modelName: ModelName): Model?
    fun update(auth: ProjectAuth)
}
