var accioApp = angular.module('accioApp', []);

accioApp.controller('TaskCtrl', function($scope, $http) {


	$scope.tasks = ["eat", "sleep", "rave", "repeat"];

	$http.get('api/tasks').success(function(data) {
		$scope.tasks = data;
	});
});
