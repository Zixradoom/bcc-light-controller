
/*
angular.
  module('bccLightController').
  config(['$locationProvider', '$routeProvider',
    function config($locationProvider, $routeProvider) {
      $locationProvider.hashPrefix('!');

      $routeProvider.
        when('/lights', {
          //template: '<light-list></light-list>'
          templateUrl: 'app/components/light-list/light-list.template.html',
          //controller: 'lightDetail'
          component: 'lightDetail'
        }).
        when('/lights/:lightId', {
          //template: '<light-detail></light-detail>'
          templateUrl: 'app/components/light-detail/light-detail.template.html'
          //controller: 'lightList'
        }).
        otherwise('/lights');
    }
  ]);
*/
angular.module('bccLightController').value('$routerRootComponent','bccLightController');

angular.module('bccLightController').
  component('bccLightController', {
    template: '<ng-outlet></ng-outlet>',
    $routeConfig: [
      {path: '/lights/',         name: 'LightList', component: 'lightList', useAsDefault: true},
      {path: '/lights/:lightId', name: 'LightDetail', component: 'lightDetail' }
    ]
  });