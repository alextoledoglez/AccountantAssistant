package com.personal.accountantAssistant.di

object MainModule {

    fun getModules() = listOf(
        DataModule.getData(),
        ManagersModule.getManagers(),
        ServicesModule.getServices(),
        ProvidersModule.getProviders(),
        UseCasesModule.getUseCases(),
        ViewModelsModule.getViewModels(),
        UtilsModule.getUtils(),
    )
}