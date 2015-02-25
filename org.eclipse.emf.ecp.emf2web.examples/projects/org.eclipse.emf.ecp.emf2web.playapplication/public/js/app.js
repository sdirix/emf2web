'use strict';

angular.module('qbForms', [
    'ngRoute',
    'ui.bootstrap',
    'ui.validate',
    'qbForms.dataServices',
    'qbForms.directives',
    'qbForms.controllers'
]).
config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.when('/:type', {
            templateUrl: '/assets/list.html',
            controller: 'ListCtrl'
        });

        $routeProvider.when('/:type/:id', {
            templateUrl: '/assets/form.html',
            controller: 'FormCtrl'
        });
        
        $routeProvider.otherwise({
            redirectTo: '/:type'
        });
    }
]);
