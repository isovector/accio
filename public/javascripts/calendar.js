accioApp.controller('CalendarCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.tasks = [];
        $scope.task = {title : "calnder"};

}]);

accioApp.directive('calendar', ['$http', '$filter', function ($http, $filter) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: "/assets/directives/calendar.partial.html",
        link: function (scope, element, attributes) {

            // Our own events object
            scope.events = [];

            scheduler.init('scheduler_here', new Date(), "month");

            // Just attaching to save for now - will have to handle onEventChange and onEventAdd later
            scheduler.attachEvents(["onEventSave"], function (id, ev) {
                id = parseInt(id);
                ev.id = id;
                // If event from calendar already exists in our local events then
                if ((cur_ev = _.findIndex(scope.events, { 'id': id })) != -1) {
                    _.extend(scope.events[cur_ev], ev); // Copy new calendar event properties to local event
                    ev = scope.events[cur_ev];          // Prepare calendar event to be sent to server by setting it to our local event 
                } else {                                // - we need to do this because the event handler from the calendar gives us back it's own event structure (ie. one missing eventType)
                    ev.id = null;
                    ev.eventType = "WorkChunk"; 
                }

                var serverEvent = jQuery.extend(true, {}, ev);
                $http.post('api/events', serverEvent).success(function (data) {
                    if (data.id != id) {
                        scheduler.changeEventId(id, data.id);
                        scope.events.push(data);
                    }
                });
                ev.id = id;
                return true;
            })

            // Deleting local event copy and database copy on deletion from calendar
            scheduler.attachEvent("onEventDeleted", function (id, ev) {
                id = parseInt(id);
                $http.delete('api/events/'+id).success(function (data) {
                    scope.events = _.without(scope.events, _.findWhere(scope.events, { id: id }));
                })
                return true;
            })

            // Exposing add event(s)
            scope.addEvent = function (ev) {
                scheduler.addEvent(ev.start_date, ev.end_date, ev.text, ev.id);
            }

            scope.addEvents = function (evs) {
                for (var i = 0; i < evs.length; i++) {
                    scope.addEvent(evs[i]);
                }
            }

            // Getting events from api, setting our events object, and adding to calendar
            scope.getEvents = function () {
                $http.get('api/events').success(function (data) {
                    scope.events = data;
                    _.forEach(scope.events, function (ev) {
                        ev.start_date = $filter('date')(ev.start_date, "dd-MM-yyyy HH:mm");
                        ev.end_date = $filter('date')(ev.end_date, "dd-MM-yyyy HH:mm");
                    });
                    (scope.events.length > 0) ? scope.addEvents(scope.events) : console.log("no events from api");
                });
            } 

            scope.runScheduler = function() {
                $http.post('api/schedule').success(function (data) {
                    scope.events = data;
                    _.forEach(scope.events, function (ev) {
                        ev.start_date = $filter('date')(ev.start_date, "dd-MM-yyyy HH:mm");
                        ev.end_date = $filter('date')(ev.end_date, "dd-MM-yyyy HH:mm");
                    });
                    (scope.events.length > 0) ? scope.addEvents(scope.events) : console.log("no events from api");
                });
            }          
        }
    }

}]);
