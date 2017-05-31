
angular.
  module('core.light').
  factory('Light', ['$resource',
    function($resource) {
      return $resource('assets/data/lights/:lightId.json', {}, {
        query: {
          method: 'GET',
          params: {lightId: 'lights'},
          isArray: true
        }
      });
    }
  ]);