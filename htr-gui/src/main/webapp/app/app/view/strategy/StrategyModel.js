
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
        'HtrGui.model.strategy.IbOrder'
    ],

    alias: 'viewmodel.htr-strategy',

    stores: {
        strategies: {
            model: 'HtrGui.model.strategy.Strategy',
            pageSize: 10
        },
        strategyPerformance: {
            model: 'HtrGui.model.strategy.StrategyPerformance',
            pageSize: 20
        },
        trades: {
            model: 'HtrGui.model.strategy.Trade',
            pageSize: 20
        },
        tradeLogs: {
            model: 'HtrGui.model.strategy.TradeLog',
            pageSize: 20
        },
        ibOrders: {
            model: 'HtrGui.model.strategy.IbOrder',
            pageSize: 20
        }
    }
});