var accioApp = angular.module('accioApp', ['ngResource']);

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

accioApp.directive('taskDetail', function() {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: "/assets/directives/taskDetail.partial.html"
	}
})
