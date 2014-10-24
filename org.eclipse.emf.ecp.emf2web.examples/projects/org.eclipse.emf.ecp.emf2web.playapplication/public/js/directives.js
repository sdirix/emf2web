'use strict';

var qbFormsDirectives = angular.module('myApp.directives', []);

qbFormsDirectives.directive('collection', function() {
    return {
        restrict: "E",
        replace: true,
        scope: {
            collection: '=',
            bindings: '=',
            openDate: '&',
            validateNumber: "&",
            validateInteger: "&"
        },
        template: '<element ng-repeat="element in collection" element="element"></element>'
    };
});


qbFormsDirectives.directive('control', function() {
    return {
        restrict: "E",
        replace: true,
        scope: {
            control: '=',
            bindings: '=',
            openDate: '&',
            validateNumber: "&",
            validateInteger: "&"
        },
        templateUrl: '/assets/templates/control.html'
    };
});


qbFormsDirectives.directive('element', function($compile) {
    return {
        restrict: "E",
        replace: true,
        scope: {
            element: '=',
            bindings: '=',
            openDate: '&',
            validateNumber: "&",
            validateInteger: "&"
        },
        //template: '<div ng-switch on="element.type" class="element.class"></div>',
        link: function(scope, element, attrs) {
            var type = scope.element.type;

            var toCompile;

            scope.element.layoutclass = "col-sm-" + scope.element.size;

            if (type === "Control") {
                toCompile = '<control control="element.elements[0]" bindings="bindings" open-date="openDate(event, element)" validate-number="validateNumber(value, element)">';

                // set the sizes for label and input
                var size = scope.element.size;

                //used when displaying labels next to the input
                //var labelsize = Math.floor(size/3);
                scope.element.elements[0].labelclass = ""; //"col-sm-" + labelsize;
                scope.element.elements[0].inputclass = ""; //"col-sm-" + (size - labelsize);

                scope.element.elements[0].wholesize = scope.element.layoutclass;

            } else if (type === "HorizontalLayout") {
            	//scope.element.layoutclass = scope.element.layoutclass + " form-inline";
                toCompile = '<fieldset ng-class="element.layoutclass">' +
                    '<div class="row">' +
                    '<collection collection="element.elements" bindings="bindings" open-date="openDate(event, element)" validate-number="validateNumber(value, element)"></collection>' +
                    '</div>' +
                    '</fieldset>';
            } else if (type === "VerticalLayout"){
                 toCompile = '<fieldset ng-class="element.layoutclass">' +

                    '<collection collection="element.elements" bindings="bindings" open-date="openDate(event, element)" validate-number="validateNumber(value, element)"></collection>' +

                    '</fieldset>';           	
            } else if (type === "Label"){
                scope.element.layoutclass = scope.element.layoutclass + " qblabel";

            	toCompile = '<div ng-class="element.layoutclass">' +
	            	'{{element.elements[0].value}}' +
	            	'</div>';
            } else{
            	toCompile = "<!-- COULD NOT DETERMINE TYPE OF ELEMENT -->";
            }

            $compile(toCompile)(scope, function(cloned, scope) {
                element.replaceWith(cloned);
            });

        }
    };
});

