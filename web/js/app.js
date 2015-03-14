// sourced from http://stackoverflow.com/questions/6274339/how-can-i-shuffle-an-array-in-javascript
function shuffle(o){ //v1.0
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
};

// sourced from http://stackoverflow.com/questions/8869248/use-cmyk-on-web-page
function cmykToRGB(c,m,y,k) {

    function padZero(str) {
        return "000000".substr(str.length)+str
    }

    var cyan = (c * 255 * (1-k)) << 16;
    var magenta = (m * 255 * (1-k)) << 8;
    var yellow = (y * 255 * (1-k)) >> 0;

    var black = 255 * (1-k);
    var white = black | black << 8 | black << 16;

    var color = white - (cyan | magenta | yellow );

    return ("#"+padZero(color.toString(16)));
}

var userID;
var savedPopup;
var savedHistory = [];
var forwardHistory = [];
var savedBookmarks = [];
var savedLikedItems = [];

angular.module('ionicApp', ['ionic'])


.factory('PoemListService', function($q, $http) {

	var poemList = []; 
	
	var res = $http.post('http://192.168.0.3:8080/getAllPoems');
	res.success(function(data, PoemListService) {
		data = shuffle(data);
		for(var i = 0; i < data.length; i++) {		
			poemList.push({
				poemID: data[i].poemID,
				title: data[i].title,
				author: data[i].author,
				text: data[i].text
			});
		}
	});
	res.error(function(data, status, headers, config) {
		alert( "failure while getting all poems: " + JSON.stringify({data: data}));
	});	
	
  return {
    poemList: poemList,
	
    getPoemList: function() {
		return poemList; //this.poemList
    },
    getPoem: function(poemId) {
		var dfd = $q.defer();
		poemList.forEach(function(poem) {
			if (poem.poemID == poemId) {
				dfd.resolve(poem);
			}
		});
		return dfd.promise
	},
	getPoemByID: function(poemId) {
			var dfd = $q.defer();
			var res = $http.post('http://192.168.0.3:8080/getPoemByID', poemId);
			res.success(function(poem) {
				dfd.resolve(poem);
				console.log("getting poem on id: " + poem.poem);
			});
			res.error(function(data, status, headers, config) {
				alert( "failure while retrieving poem: " + JSON.stringify({data: data}));
			});
			//console.log("Returnin: \n" + JSON.stringify({data: dfd.promise}));
			return dfd.promise
	}
  }
})

.config(function($stateProvider, $urlRouterProvider) {
	
	$stateProvider
		.state('login', {
			url: '/login',
			templateUrl: 'login.html',
			controller: 'LoginController'
		})

		.state('home', {
			url: '/home',
			templateUrl: 'home.html',
			resolve: {
				poemList: function(PoemListService) {
					return PoemListService.getPoemList()
				}
			},
			controller: 'HomeController'
		})
		
		.state('main', {
			url : '/main',
			templateUrl : 'mainContainer.html',
			abstract : true,
			controller : 'MainController'
		})		
		
		.state('main.poem', {
			url: '/poem/:poemId',
			resolve: {
				poem: function($stateParams, PoemListService) {
					return PoemListService.getPoem($stateParams.poemId)
				}
			},
			views: {
				'main': {
					templateUrl: 'poem.html',
					controller: 'PoemViewController'
				}
			}
		})
		
		.state('compose', {
			url: '/compose',
			templateUrl: 'compose.html'
		})
		
		.state('newPoem', {
			url: '/newPoem',
			templateUrl: 'newPoem.html',
			controller: 'NewPoemCtrl'
		})
		
		.state('bookmarks', {
			url: '/bookmarks',
			templateUrl: 'bookmarks.html',
			controller: 'BookmarksCtrl'
		});
		
	$urlRouterProvider.otherwise("/login");
	
})

.controller('LoginController', function($scope, $state) {
	$scope.navTitle = 'Login Page';
	
	$scope.login = function(username) {
		userID = username;
		$state.go('home');
	};
})


.controller('BookmarksCtrl', function($scope) {
	$scope.last = savedHistory[savedHistory.length - 1];
	$scope.bookmarks = savedBookmarks;
	
	$scope.wipeHistory = function(poemID) {
		forwardHistory = [];
		savedHistory = [poemID];
	};
	
})


.controller('MainController', function($scope, $state, $ionicSideMenuDelegate) {
	
	$scope.backToList = function() {
		forwardHistory = [];
		savedHistory = [];
		toggleLeft();
		$state.go('home');
	};
	
	$scope.toBookmarksPage = function() {
		toggleLeft();
		$state.go('bookmarks');
	};
	
	
	
	toggleLeft = function() {
		$ionicSideMenuDelegate.toggleLeft();
	};
})

/*
('NewPoemCtrl', function($scope, $ionicPopup, $http) {
	
	$scope.submitConfirm = function() {
		var confirmPopup = $ionicPopup.confirm({
			title: 'Are you sure you want to submit this poem?',
			template: 'test template'
		});
		confirmPopup.then(function(res) {
			if(res) {
				console.log('You are sure');
				submitPoem();
			} else {
				console.log('You are not sure');
			}
		});
	};
		
		
	submitPoem = function() {	
		
		// JSON of poem
		var poem = {
			author: $scope.poem.author,
			title: $scope.poem.title,
			text: $scope.poem.text
		};
		
		// Posting to server
		var res = $http.post('http://192.168.0.3:8080/savePoem_json', poem);
		res.success(function(data, status, headers, config) {
			$scope.message = data;
		});
		res.error(function(data, status, headers, config) {
			alert( "failure message: " + JSON.stringify({data: data}));
		});		
		
		//Making the fields empty
		$scope.poem.author='';
		$scope.poem.title='';
		$scope.poem.text='';
		
	};
})
*/

.controller('PoemViewController', function($scope, poem, $http, $state, $ionicPopup, $ionicScrollDelegate, $filter, $ionicSideMenuDelegate) {
	
	
	console.log("ENTERING VIEW ON POEM: " + poem.poemID);
	
	$scope.poem = poem;
	$scope.list = [];
	$scope.likeClass = 'button button-stable icon ion-bookmark';
	$scope.likedItems = savedLikedItems;
	$ionicSideMenuDelegate.canDragContent(false);
	
	var orderBy = $filter('orderBy');
	var choiceStatus = -1;
	
	$scope.toHomePage = function() {
		$state.go('home');
	};	
	
	$scope.toggleLeft = function() {
		$ionicSideMenuDelegate.toggleLeft();
	};	
	
 $scope.showPopup = function() {
   $scope.data = {}

   // An elaborate, custom popup
    savedPopup = $ionicPopup.show({
     templateUrl: 'newListPopup.html',
     title: 'Related Poems',
     scope: $scope,
     buttons: [
       { text: 'Back' },
       {
         text: '<b>Shuffle</b>',
         type: 'button-positive',
         onTap: function(e) {
					e.preventDefault();
					mulligan();
				}
       }
     ]
   });
  };
  
  
  $scope.closePopup = function() {
    savedPopup.close()
  }
	
	$scope.findSimilar = function() {
		choiceStatus = 0;
		var res = $http.post('http://192.168.0.3:8080/findSimilar', savedHistory.concat(forwardHistory));
		res.success(function(data) {
			savedHistory.push(data.poemID);
			changeScope(data);
			poem = data;
		});
		scrollTop();
		res.error(function(data, status, headers, config) {
			alert( "failure message: " + JSON.stringify({data: data}));
		});	
		forwardHistory = [];
	};
	
	
	$scope.forward = function() {
		if(forwardHistory.length != 0) {
			getNewList();
			savedHistory.push(forwardHistory.pop());
			scrollTop();
			var res = $http.post('http://192.168.0.3:8080/getPoemByID', savedHistory[savedHistory.length-1]);
			res.success(function(data) {
				changeScope(data);
			});
			res.error(function(data, status, headers, config) {
				alert( "failure while going forward: " + JSON.stringify({data: data}));
			});
		}
	};
	
	$scope.back = function() {
		if (savedHistory.length > 1) {
			getNewList();
			forwardHistory.push(savedHistory.pop());
			scrollTop();
			var res = $http.post('http://192.168.0.3:8080/getPoemByID', savedHistory[savedHistory.length-1]);
			res.success(function(data) {
				changeScope(data)
			});
			res.error(function(data, status, headers, config) {
				alert( "failure while going back: " + JSON.stringify({data: data}));
			});	
		}
	};
	
	$scope.openNew = function(item) {
		savedHistory.push(item.poemID);
		choiceStatus = 1;
		savedPopup.close();
		scrollTop();
		poem = item;
		changeScope(item);
		getNewList();
		forwardHistory = [];
	};
	
	changeScope = function(item) {
		changeStar(item.poemID);
		$scope.poem = item;
	};


	mulligan = function() {
		//$scope.closePopup();
		var res = $http.post('http://192.168.0.3:8080/getRandDistPoems', savedHistory.concat(forwardHistory));
		temp = [];
		res.success(function(data) {
			for(var i = 0; i < data.length; i++) {
//				var color = (data[i].distribution > 0.25) ? 1 : 1 * data[i].distribution / 0.25;
				var color = (data[i].distribution > 0.3) ? 1 : data[i].distribution<0.05?0: (data[i].distribution-0.05) / 0.25;
				temp.push({
					poemID: data[i].poemID,
					title: data[i].title,
					author: data[i].author,
					text: data[i].text,
					distribution: data[i].distribution,
					distrColor: cmykToRGB(color, color, 0.2, 0)
				});
			}
			$scope.list = temp;
			$scope.list = orderBy($scope.list, '-distribution', false);
		});

		res.error(function(data, status, headers, config) {
			alert( "failure while running mulligan: " + JSON.stringify({data: data}));
		});	
		//$scope.showPopup();
	};

	
	
	getNewList = function() {
		var tempHistory = savedHistory;
		tempHistory.push(poem.poemID);
		var res = $http.post('http://192.168.0.3:8080/getRandDistPoems', tempHistory);
		$scope.list = [];
		
		if (tempHistory.length > 1)
			tempHistory.pop();
		
		res.success(function(data) {
			for(var i = 0; i < data.length; i++) {
//				var color = (data[i].distribution > 0.25) ? 1 : 1 * data[i].distribution / 0.25;
				var color = (data[i].distribution > 0.3) ? 1 : data[i].distribution<0.05?0: (data[i].distribution-0.05) / 0.25;
				$scope.list.push({
					poemID: data[i].poemID,
					title: data[i].title,
					author: data[i].author,
					text: data[i].text,
					distribution: data[i].distribution,
					distrColor: cmykToRGB(color, color, 0.2, 0)
				});
			}
			$scope.list = orderBy($scope.list, '-distribution', false);
		});

		res.error(function(data, status, headers, config) {
			alert( "failure while getting New list: " + JSON.stringify({data: data}));
		});	
	};

	$scope.like = function(id) {
		if (checkLiked(id) == -1) {
			
			changeStarToLiked();

			savedBookmarks.push(poem);
			savedLikedItems.push(id);
			$scope.likedItems.push(id);
			
			
			var likedPoem = {userId: userID, poemId: poem.poemID, weight: poem.distribution, afterSimilar: choiceStatus};
			
			var res = $http.post('http://192.168.0.3:8080/likePoem', likedPoem);
			res.success(function(data) {
				console.log("Poem Liked: " + id);
			});	
			res.error(function(data, status, headers, config) {
				alert( "failure while liking poem: " + JSON.stringify({data: data}));
			});	
		};
	}
	
	changeStar = function(id) {
		if (checkLiked(id) == -1) {
			changeStarToUnliked();
		} else {
			changeStarToLiked();
		}
	}
	
	checkLiked = function(id) {
		return savedLikedItems.indexOf(id);
	};
	
	changeStarToLiked = function() {
		$scope.likeClass = 'button icon button-balanced ion-bookmark';
	};
	
	changeStarToUnliked = function() {
		$scope.likeClass = 'button icon button-stable ion-bookmark';
	};
	
	scrollTop = function() {
		$ionicScrollDelegate.scrollTop();
	};

	changeStar(poem.poemID);
	getNewList();
})

.controller('HomeController', function($scope, $state, poemList) {
	$scope.currentIndex = 50;
	
	
	/*
	$scope.leftButtons = [{
		type: 'button-icon icon ion-navicon',
		tap: function(e) {
			$scope.toggleMenu();
		}
	}];
	*/
	$scope.currentList = poemList;
	
	$scope.showMore = function() {
		$scope.currentIndex += 50;
		if ($scope.currentIndex >= poemList.length)
			$scope.currentIndex = poemList.length;
		$scope.$broadcast('scroll.infiniteScrollComplete');
	};
	
	/*
	$scope.showMore = function() {
		for (var i = $scope.currentList.length; (i < currentIndex && i < poemList.length); i++) {
			console.log(i);
			$scope.currentList.push(poemList[i]);
		}
		currentIndex += 10;
	};
	
	$scope.showMore();
	*/
});


