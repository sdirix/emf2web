'use strict';

/* Services */
var dataServices = angular.module('myApp.dataServices', []);
var utilityServices = angular.module('myApp.utilityServices', []);

var maxSize = 99;

//http://stackoverflow.com/questions/14430655/recursion-in-angular-directives
//TODO: Maybe Use https://github.com/marklagendijk/angular-recursion ? 
dataServices.factory('RecursionHelper', ['$compile',
    function($compile) {
        return {
            compile: function(element, link){
                // Normalize the link parameter
                if(angular.isFunction(link)){
                    link = { post: link };
                }

                // Break the recursion loop by removing the contents
                var contents = element.contents().remove();
                var compiledContents;
                return {
                    pre: (link && link.pre) ? link.pre : null,
                    /**
                     * Compiles and re-adds the contents
                     */
                    post: function(scope, element){
                        // Compile the contents
                        if(!compiledContents){
                            compiledContents = $compile(contents);
                        }
                        // Re-add the compiled contents to the element
                        compiledContents(scope, function(clone){
                            element.append(clone);
                        });

                        // Call the post-linking function, if any
                        if(link && link.post){
                            link.post.apply(null, arguments);
                        }
                    }
                };
            }
        };
    }
]);

dataServices.factory('SendData', ['$http',
    function($http) {
        return {
            sendData: function(type, id, data) {
                if (id !== "") {
                    data.id = id;
                    $http.post("/" + type + "/" + id, data).success(function() {
                        alert("Update Data successfull");
                    }).error(function(){
                        alert("Update Data failed!");
                    });
                } else {
                    $http.post("/" + type, data).success(function() {
                        alert("Create Data successfull");
                    }).error(function(){
                        alert("Create Data failed!");
                    });
                }
            }
        };
    }
]);

dataServices.factory('GetData', ['$http', '$q',
    function($http, $q) {
        return {
            getFormData: function(type, id) {

                var defer = $q.defer();

                var viewPromise = $http.get("/" + type + "/view");
                var modelPromise = $http.get("/" + type + "/model");
                var dataPromise = $http.get("/" + type);

                $q.all([viewPromise, modelPromise, dataPromise]).then(function(values) {
                    var viewModelData = values[0].data;
                    var ecoreModelData = values[1].data;
                    var rawInstanceData = values[2].data;
                    var instanceData = getInstanceWithID(rawInstanceData, id);

                    var result = {
                        "id": "",
                        "layoutTree": [],
                        "bindings": {}
                    };

                    var bindings = {};
                    var layoutTree = buildLayoutTree(ecoreModelData, viewModelData, instanceData, bindings);

                    //check for id in instanceData
                    if (instanceData !== undefined && instanceData.id !== undefined) {
                        result.id = instanceData.id;
                    }

                    result.layoutTree = layoutTree;
                    result.bindings = bindings;

                    defer.resolve(result);
                });

                return defer.promise;
            },

            getAllRawData: function(type, id) {
                var defer = $q.defer();

                var viewPromise = $http.get("/" + type + "/view");
                var modelPromise = $http.get("/" + type + "/model");
                var dataPromise = $http.get("/" + type);

                $q.all([viewPromise, modelPromise, dataPromise]).then(function(values) {
                    var viewModelData = values[0].data;
                    var ecoreModelData = values[1].data;
                    var rawInstanceData = values[2].data;
                    var instanceData = getInstanceWithID(rawInstanceData, id);

                    var result = {
                        "model": ecoreModelData,
                        "layout": viewModelData,
                        "instance": instanceData
                    };

                    //check for id in instanceData
                    if (instanceData !== undefined && instanceData.id !== undefined) {
                        result.id = instanceData.id;
                    }

                    defer.resolve(result);
                });

                return defer.promise;
            },

            getRawInstanceData: function(type, id) {
                var defer = $q.defer();

                var dataPromise = $http.get("/" + type);

                $q.all([dataPromise]).then(function(values) {
                    var result;
                    var rawInstanceData = values[0].data;
                    if (id === undefined) {
                        result = rawInstanceData;
                    } else {
                        result = getInstanceWithID(rawInstanceData, id);
                    }
                    defer.resolve(result);
                });

                return defer.promise;
            },

            getRawModelData: function(type) {
                var defer = $q.defer();

                var dataPromise = $http.get("/" + type + "/model");

                $q.all([dataPromise]).then(function(values) {
                    var rawPersonData = values[0].data;
                    defer.resolve(rawPersonData);
                });

                return defer.promise;
            }
        };
    }
]);

function buildLayoutTree(model, layout, instance, bindings) {
    var result = [];

    if (layout.elements === undefined) {
        return result;
    }

    for (var i = 0; i < layout.elements.length; i++) {
        var element = layout.elements[i];

        if (element.type === "QBVerticalLayout") {
            var vLayoutObject = {
                "type": "VerticalLayout",
                "elements": buildLayoutTree(model, element, instance, bindings),
                "size": maxSize
            };

            //no need to change sizes
            result.push(vLayoutObject);

        } else if (element.type === "QBHorizontalLayout") {
            var hLayoutObject = {
                "type": "HorizontalLayout",
                "elements": buildLayoutTree(model, element, instance, bindings),
                "size": maxSize
            };

            //change sizes
            var size = hLayoutObject.elements.length;
            var individualSize = Math.floor(maxSize / size);
            for (var j = 0; j < hLayoutObject.elements.length; j++) {
                hLayoutObject.elements[j].size = individualSize;
            }

            result.push(hLayoutObject);
        } else if (element.type === "Control") {
            var cObject = {
                "type": element.type,
                "elements": [],
                "size": maxSize
            };

            var elementPath = element.path;
            var elementName = element.name;
            if(elementName === undefined || elementName === null){
                elementName = elementPath;
            }

            var elementTypeInfo = getType(elementPath, model);
            var instanceValue = getValue(elementPath, instance); 

            var uiElement = getUIElement(elementName, elementPath, elementTypeInfo, instanceValue);
            cObject.elements.push(uiElement);

            result.push(cObject);

            bindings[uiElement.name] = uiElement.value;
        } else if (element.type === "Label") {
            var lObject = {
                "type": element.type,
                "elements": [],
                "size": maxSize
            };

            var uiElement = getUIElement("", "", {type:"Label"}, element.text);
            lObject.elements.push(uiElement);

            result.push(lObject);
        }
    }

    return result;
}

function getInstanceWithID(instanceData, id) {
    if (id === "Create") {
        return undefined;
    }
    for (var i = 0; i < instanceData.length; i++) {
        var instance = instanceData[i];
        if (id === instance.id) {
            return instance;
        }
    }
    return undefined;
}

function getType(elementName, model) {
    var properties = model.properties;
    var propertiesKeys = Object.keys(properties);

    var result = {
        "type": "",
        "enum": []
    };

    for (var i = 0; i < propertiesKeys.length; i++) {
        var key = propertiesKeys[i];
        if (key === elementName) {
            var type = properties[key].type;
            //string can be "more" than string
            if (type === "string") {
                if (properties[key].format !== undefined) {
                    var format = properties[key].format;
                    if (format === "date-time") {
                        result.type = "date-time";
                        return result;
                    }
                }

                if (properties[key].enum !== undefined) {
                    result.type = "enum";
                    result.enum = properties[key].enum;
                    return result;
                }
            }

            result.type = properties[key].type;
            return result;
        }
    }

    return result;
}

function getValue(elementName, instanceData) {
    if (instanceData === undefined) {
        return null;
    }

    var keys = Object.keys(instanceData);

    for (var i = 0; i < keys.length; i++) {
        var key = keys[i];
        if (key === elementName) {
            return instanceData[key];
        }
    }

    return null;
}

function getUIElement(displayName, name, type, value) {
    var data = {
        "displayname": displayName,
        "name": name,
        "value": value,
        "type": type.type,
        "options": type.enum,
        "isOpen": false,
        "alerts": []
    };
    return data;
}
