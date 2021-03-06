define(['app'], function(app){
    app.factory('MatchService', function($http) {
        var matchService = {
            loadMatchList : function() {
                var promise = $http.get('/match/list').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            saveMatch : function(data) {
                var promise = $http.post('/match/info', data).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            loadNameList : function($query){
                return $http.get('/match/names', { cache: true}).then(function(response) {
                    var data = response.data.data;
                    console.log(data);
                    return data.filter(function(item) {
                        if(item.email){
                            return item.email.toLowerCase().indexOf($query.toLowerCase()) != -1;
                        }
                    });
                });
            },
            loadMatch : function(config) {
                var promise = $http.get('/match/info',config).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            getPercent : function(config) {
                var promise = $http.get('/match/percent',config).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            getRecent : function(config) {
                var promise = $http.get('/match/recent',config).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            loadSuggestion: function(config) {
                var promise = $http.get('/suggestion/agg-list',config).then(function (response) {
                    return response.data;
                });
                return promise;
            },
            loadPortfolios: function(config){
                var promise = $http.get('/match/queryPortfolio',config).then(function(response){
                    return response.data;
                });
                return promise;
            },
            loadCusips: function(config){
                var promise = $http.get('/match/queryCusip',config).then(function(response){
                    return response.data;
                });
                return promise;
            },
            smartMatch: function(config){
                var promise = $http.get('/match/smartMatch',config).then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
        return matchService;
    });
});