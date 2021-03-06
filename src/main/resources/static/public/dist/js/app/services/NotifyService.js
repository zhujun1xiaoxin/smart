define(['app', 'sockjs', 'stomp'], function (app,SockJS, Stomp) {
    app
    //.constant('sockJsProtocols', ["xhr-streaming", "xhr-polling"]) // only allow XHR protocols
    .constant('sockJsProtocols', [])
    .factory('StompClient', ['sockJsProtocols', '$q', function (sockJsProtocols, $q) {
        var stompClient;
        var wrappedSocket = {
            init: function (url) {
                if (sockJsProtocols.length > 0) {
                    stompClient = Stomp.over(new SockJS(url, null, {transports: sockJsProtocols}));
                }
                else {
                    stompClient = Stomp.over(new SockJS(url));
                }
            },
            connect: function () {
                return $q(function (resolve, reject) {
                    if (!stompClient) {
                        reject("STOMP client not created");
                    } else {
                        stompClient.connect({}, function (frame) {
                            resolve(frame);
                        }, function (error) {
                            reject("STOMP protocol error " + error);
                        });
                    }
                });
            },
            disconnect: function() {
                stompClient.disconnect();
            },
            subscribe: function (destination) {
                var deferred = $q.defer();
                if (!stompClient) {
                    deferred.reject("STOMP client not created");
                } else {
                    stompClient.subscribe(destination, function (message) {
                        deferred.notify(JSON.parse(message.body));
                    });
                }
                return deferred.promise;
            },
            subscribeSingle: function (destination) {
                return $q(function (resolve, reject) {
                    if (!stompClient) {
                        reject("STOMP client not created");
                    } else {
                        stompClient.subscribe(destination, function (message) {
                            resolve(JSON.parse(message.body));
                        });
                    }
                });
            },
            send: function (destination, headers, object) {
                stompClient.send(destination, headers, object);
            }
        };
        return wrappedSocket;
    }])
    .factory('NotifyService', ['StompClient', '$q', function (stompClient) {

        return {
            connect: function (url) {
                stompClient.init(url);
                return stompClient.connect().then(function (frame) {
                    return frame.headers['user-name'];
                });
            },
            disconnect: function() {
                stompClient.disconnect();
            },
            loadNotification: function() {
                return stompClient.subscribeSingle("/app/notification");
            },
            loadMessage: function() {
                return stompClient.subscribeSingle("/app/message");
            },
            loadTask: function() {
                return stompClient.subscribeSingle("/app/task");
            },
            fetchMessageUpdateStream: function () {
                return stompClient.subscribe("/user/queue/message");
            }
        };

    }]);
});