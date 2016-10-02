
/**
 * Created by robertk on 4.7.2016.
 */
Ext.define('HtrGui.view.strategy.StrategyModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'HtrGui.model.strategy.Strategy',
        'HtrGui.model.strategy.StrategyPerformance',
        'HtrGui.model.strategy.Trade',
        'HtrGui.model.strategy.TradeLog',
        'HtrGui.model.strategy.TradeIbOrder',
        'HtrGui.model.strategy.IbOrder',
        'HtrGui.model.strategy.IbOrderEvent'
    ],

    alias: 'viewmodel.htr-strategy',

    stores: {
        strategies: {
            model: 'HtrGui.model.strategy.Strategy',
            pageSize: 10
        },
        strategyPerformances: {
            model: 'HtrGui.model.strategy.StrategyPerformance',
            pageSize: 10
        },
        trades: {
            model: 'HtrGui.model.strategy.Trade',
            pageSize: 10
        },
        tradeLogs: {
            model: 'HtrGui.model.strategy.TradeLog',
            pageSize: 10
        },
        ibOrders: {
            model: 'HtrGui.model.strategy.IbOrder',
            pageSize: 20
        }
    }
});