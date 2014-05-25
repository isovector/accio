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

	$scope.update();
}]);
