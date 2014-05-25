accioApp.controller('CalendarCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.tasks = [];
        $scope.task = {title : "calnder"};

        $scope.test = function(){
                alert("jj");
        }

        
        //scheduler.init('scheduler_here', new Date(), "month");

        

        //$scope.scheduler = scheduler;
}]);

accioApp.directive('calendar', ['$http', function ($http) {
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

            scheduler.attachEvent("onEventSave", function (id, ev) {
                ev["id"] = id;
                console.log(ev);
                if ((cur_event = _.findIndex(scope.events, { 'id': id })) != -1) {
                    events[cur_ev] = ev;
                } else {
                    scope.events.push(ev);
                }
                // Callback to events.add api here. sth like below
                $http.post('api/events', ev).success(function (data) {
                    console.log(data);
                });
                return true;
            })

            // Exposing add/delete event(s)
            scope.addEvent = function (ev) {
                scheduler.addEvent(ev);
            }

            scope.addEvents = function (evs) {
                for (ev in evs) {
                    scope.addEvent(ev);
                }
            }

            // Getting events from api, setting our events object, and adding to calendar
            scope.getEvents = function () {
                $http.get('api/events').success(function (data) {
                    console.log(data);
                    scope.events = data;
                });
                (scope.events.length > 0) ? scope.addEvents(events) : console.log("no events from api");

            }
            // on button click get events from server
            // and then parse them to calendar              
        }
    }

}]);
