accioApp.controller('CalendarCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.tasks = [];
        $scope.task = {title : "calnder"};

        $scope.test = function(){
                alert("jj");
        }

        
        //scheduler.init('scheduler_here', new Date(), "month");

        

        //$scope.scheduler = scheduler;
}]);

accioApp.directive('calendar', ['$http', '$filter', function ($http, $filter) {
    return {
        //Only apply this directive to elements with this name
        restrict: 'A',
        //replace the element with the template
        replace: true,
        templateUrl: "/assets/directives/calendar.partial.html",
        link: function (scope, element, attributes) {

            // Our own events object
            scope.events = [];

            scheduler.init('scheduler_here', new Date(), "month");

            scheduler.attachEvents(["onEventSave", "onEventChanged"], function (id, ev) {
                console.log(ev);
                if ((cur_ev = _.findIndex(scope.events, { 'id': id })) != -1) {
                    scope.events[cur_ev] = ev;

                } else {
                    ev.id = "-1";
                    ev.eventType = "Normal";
                    
                }
                // Callback to events.add api here. sth like below
                $http.post('api/events', ev).success(function (data) {
                    if (data.id != id) {
                        scheduler.changeEventId(id, data.id);
                        scope.events.push(data);
                    }
                    console.log(data);
                });
                return true;
            })

            //scheduler.attachEvent("onEventDeleted", function (id, ev) {
                //console.log("The id: " + id);
                //console.log(ev);
                //return true;
            //})

            // Exposing add/delete event(s)
            scope.addEvent = function (ev) {
                scheduler.addEvent(ev);
            }

            scope.addEvents = function (evs) {
                for (var i = 0; i < evs.length; i++) {
                    scope.addEvent(evs[i]);
                }
            }

            // Getting events from api, setting our events object, and adding to calendar
            scope.getEvents = function () {
                $http.get('api/events').success(function (data) {
                    console.log(data);
                    scope.events = data;
                    _.forEach(scope.events, function (ev) {
                        ev.start_date = $filter('date')(ev.start_date, "dd-MM-yyyy HH:mm");
                        ev.end_date = $filter('date')(ev.end_date, "dd-MM-yyyy HH:mm");
                    });
                    (scope.events.length > 0) ? scope.addEvents(scope.events) : console.log("no events from api");
                });
                

            }
            // on button click get events from server
            // and then parse them to calendar              
        }
    }

}]);
