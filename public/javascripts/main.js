var accioApp = angular.module('accioApp', []);

accioApp.controller('TaskCtrl', function($scope, $http) {


	$scope.tasks = ["eat", "sleep", "rave", "repeat"];
	$scope.task = {title : ""};

	$scope.update = function() {
		$http.get('api/tasks').success(function(data) {
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
});
