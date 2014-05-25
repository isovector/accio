var accioApp = angular.module('accioApp', ['ngResource']);

/*accioApp.config(["$routeProvider", function($routeprovider){
	return $routeProvider
		.when('/', {
			templateUrl: 'views/tasks.scala.html',
			controller: 'TaskCtrl'
		})
		.when('/calendar', {
			templateUrl: 'views/calendar',
			controller: 'CalendarCtrl'
		})
		.when('/testroute', {
			templateUrl: 'views/broken',
			//controller: 'CalendarCtrl'
		})
		.otherwise({
			redirectTo: '/'
		})
	});*/
		

accioApp.factory('Task', ['$resource', function($resource){
		return $resource('api/tasks/:taskId', {}, {
			getTasks: {method:'GET', },
			createTask: {method:'POST', params: {title : '@title'}},
			//deleteTask: {method:'DELETE', params: {"taskId" : taskId}}
		});
	}
]);

accioApp.controller('TaskCtrl', ['$scope', '$http', 'Task', function($scope, $http, Task) {
		
	$scope.tasks = [];
	$scope.task = {title : ""};

	$scope.update = function() {
		Task.getTasks(function(data) {
			console.log(data);
			$scope.tasks = data;
		});		
	}


	$scope.addTask = function() {
		//This will be the real post parameter
		//"{title : 'hello world'}"
		$http.post('api/tasks', $scope.task).success(function(data) {
			console.log(data);
			$scope.update();
		});
	}

	$scope.deleteTask = function(id) {
		$http.delete('api/tasks/'+id).success(function() {
			$scope.update();
		});
	}

	$scope.update();
}]);

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
		templateUrl: "/assets/directives/calendar.partial.html"
	}
})
