var accioApp = angular.module('accioApp', ['ngResource']);

accioApp.factory('Task', ['$resource', function($resource){
		return $resource('api/tasks/:taskId', {}, {
			getTasks: {method:'GET', isArray:true},
			createTask: {method:'POST', params: {title : '@title'}},
                        editTaskTitle: {method:'POST', params: {title : '@title'}}
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
                dueDate : "",
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

        $scope.editTaskTitle = function(id) {
                //This will be the real post parameter
                //"{title : 'hello world'}"
                $http.post('api/tasks/'+id).success(function() {
                        $scope.update();
                });
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
})

accioApp.directive('taskList', function() {
	return {
		//Only apply this directive to elements with this name
		restrict: 'A',
		//replace the element with the template
		replace: true,
		templateUrl: "/assets/directives/taskList.partial.html"
	}
})

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
				var serverTask = {title : scope.task.name, description: scope.task.description, dueDate : scope.task.dueDate, estimatedTime: scope.task.estimatedTime};
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
}])
