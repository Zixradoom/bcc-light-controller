angular.
  module('lightList').
  component('lightList', {
    templateUrl: 'app/components/light-list/light-list.template.html',
    controller: ['Light',
      function LightListController(Light) {
        this.lights = Light.query();
        this.orderProp = 'id';
      }
    ]
  });