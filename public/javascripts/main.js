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
			getTasks: {method:'GET', isArray:true},
			createTask: {method:'POST', params: {title : '@title'}},
			//deleteTask: {method:'DELETE', params: {"taskId" : taskId}}
		});
	}
]);

//This service acts as our task repository/ source of task truth
accioApp.service('TaskService', ['Task', '$http', '$rootScope', function(Task, $http, $rootScope) {
	var service = {
		tasks : [],

		refreshTasks : function() {
			Task.getTasks(function(data) {
				console.log(data);
				service.tasks = data;
				$rootScope.$broadcast('tasks.update');
			});
		}
	}
	return service;
}])

accioApp.controller('TaskCtrl', ['$scope', '$http', 'TaskService', function($scope, $http, TaskService) {

	$scope.tasks = [];
	$scope.selectedTask = null;

	var emptyTask = {name : "", 
		description : "", 
		estimatedTime : 0,
		subtasks : null,
		editMode : true};

	$scope.update = function() {
		TaskService.refreshTasks();
	}

	$scope.createTask = function() {
		//Set our selected task to our new task so it can be edited
		$scope.selectedTask = angular.copy(emptyTask);
	}

	$scope.deleteTask = function(id) {
		$http.delete('api/tasks/'+id).success(function() {
			$scope.update();
		});
	}

	$scope.$on('tasks.update', function (event) {
		$scope.tasks = TaskService.tasks;
	})

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
		templateUrl: "/assets/directives/calendar.partial.html",
		link: function(scope, element, attributes) {
			scheduler.init('scheduler_here', new Date(), "month");
		// on button click get events from server
		// and then parse them to calendar		
		}
	}
	
});

accioApp.directive('categoryList', function() {
	return {
		//Only apply this directive to elements with this name
		restrict: 'A',
		//replace the element with the template
		replace: true,
		templateUrl: "/assets/directives/categoryList.partial.html",
		link: function(scope, element, attributes) {
			scope.categories = ["EAT", "SLEEP", "RAVE", "REPEAT"];
		}

	}
});

accioApp.directive('taskList', function() {
	return {
		//Only apply this directive to elements with this name
		restrict: 'A',
		//replace the element with the template
		replace: true,
		templateUrl: "/assets/directives/taskList.partial.html"
	}
});

accioApp.directive('taskDetail', ['Task', '$http', 'TaskService', function(Task, $http, TaskService) {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: "/assets/directives/taskDetail.partial.html",
		scope : {
			task : '=selectedTask'
		},
		link: function(scope, element, attributes) {
			
			scope.saveTask = function() {
				//Task.createTask
				//For now, just send a task with the title so the server will accept it
				var serverTask = {title : scope.task.name};
				console.log(serverTask);
				$http.post('api/tasks', serverTask).success(function(data) {
					console.log(data);
					TaskService.refreshTasks()
				});
				scope.task.editMode = false;
			}

			scope.editTask = function() {
				scope.task.editMode = true;
			}
		}	
	}
}]);
