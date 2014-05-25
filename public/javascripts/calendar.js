accioApp.controller('CalendarCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.tasks = [];
        $scope.task = {title : "calnder"};

        $scope.test = function(){
                alert("jj");
        }

        
        scheduler.init('scheduler_here', new Date(), "month");

        //$scope.scheduler = scheduler;
}]);

accioApp.directive('calendar', function() {
        return {
                //Only apply this directive to elements with this name
                restrict: 'A',
                //replace the element with the template
                replace: true,
                templateUrl: "/assets/directives/calendar.partial.html",
                link: function(scope, element, attributes) {
                        scheduler.init('scheduler_here', new Date(), "month");
                // on button click get events from server
                // and then parse them to calendar              
                }
        }
        
})
