accioApp.controller('CalendarCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.tasks = [];
        $scope.task = {title : "calnder"};

        $scope.test = function(){
                alert("jj");
        }

        
        //scheduler.init('scheduler_here', new Date(), "month");

        

        //$scope.scheduler = scheduler;
}]);

accioApp.directive('calendar', function() {
        return {
                //Only apply this directive to elements with this name
                restrict: 'A',
                //replace the element with the template
                replace: true,
                templateUrl: "/assets/directives/calendar.partial.html",
                link: function (scope, element, attributes) {

                    // Our own events object
                    $scope.events = [];

                    scheduler.init('scheduler_here', new Date(), "month");

                    scheduler.attachEvent("onEventSave", function (id, ev) {
                        console.log(ev);
                        events.push(ev);
                        // Callback to events.add api here. sth like below
                        /*$http.post('api/events', ev).success(function (data) {
                            console.log(data);
                        });*/
                        return true;
                    })

                    // Exposing add event(s)
                    $scope.addEvent = function (ev) {
                        scheduler.addEvent(ev);
                    }

                    $scope.addEvents = function (evs) {
                        for(ev in evs) {
                            addEvent(ev);
                        }
                    }

                    // Getting events, setting our events object, and adding to calendar
                    $scope.getEvents = function () {
                        $http.get('api/events').success(function (data) {
                            console.log(data);
                            events = data;
                        });
                        (events.length() > 0) ? addEvents(events) : console.log("no events from api");

                    }
                // on button click get events from server
                // and then parse them to calendar              
                }
        }
        
})
