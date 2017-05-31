
angular.
  module('lightDetail').
  component('lightDetail', {
    templateUrl: 'app/components/light-detail/light-detail.template.html',
    controller: [ 'Light', '$http',
      function LightDetailController(Light,$http) {
        var self = this;
        
        this.$routerOnActivate = function(next, previous){
          var id = next.params.lightId;
          var temp = Light.get({lightId: id}, function(light) {
            self.light = light;
          });
          return temp;
        }
        
        this.on = function() {
          $http({
            method: 'POST',
            url: 'srv/individualLightState',
            data: {
              providerId: 'b0b9f292-ef65-42e0-ba89-3f9e82a0290a',
              controllerId: 0,
              lightId: self.light.id,
              state: true
            }
          })
        };
        
        this.off = function() {
          $http({
            method: 'POST',
            url: 'srv/individualLightState',
            data: {
              providerId: 'b0b9f292-ef65-42e0-ba89-3f9e82a0290a',
              controllerId: 0,
              lightId: self.light.id,
              state: false
            }
          })
        };
        
        this.gotoLights = function() {
          self.$router.navigate(['LightList']);
        };
      }
    ],
    bindings: { $router: '<' }
  });