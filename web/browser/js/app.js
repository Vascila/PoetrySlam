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
var savedBookmarks = [];
var savedLikedItems = [];

angular.module('ionicApp', ['ionic'])

.factory('PoemListService', function($q, $http) {

	var poemList = []; 
	
	var res = $http.get('http://localhost:8080/getAllPoems');
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
			var res = $http.post('http://localhost:8080/getPoemByID', poemId);
			res.success(function(poem) {
				dfd.resolve(poem);
				console.log("getting poem on id: " + poem.poem);
			});
			res.error(function(data, status, headers, config) {
				alert( "failure while retrieving poem: " + JSON.stringify({data: data}));
			});
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


.controller('BookmarksCtrl', function($scope, $state, $ionicHistory) {
	$scope.last = savedHistory[savedHistory.length - 1];
	$scope.bookmarks = savedBookmarks;
	
	$scope.goToPoem = function(item) {
		$ionicHistory.clearCache();
		savedHistory = [item];
		console.log("PASSING ARGUMENT: " + item);
		$state.go('main.poem', {poemId: item});
	};
	
	$scope.backToLastPoem = function() {
		$ionicHistory.goBack();
	};
	
	$scope.wipeHistory = function(poemID) {
		savedHistory = [poemID];
	};
	
})


.controller('MainController', function($scope, $state, $ionicSideMenuDelegate, $ionicHistory) {
	
	$scope.backToList = function() {
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


.controller('PoemViewController', function($ionicSlideBoxDelegate, $scope, poem, $http, $ionicPopup, $ionicScrollDelegate, $filter, $ionicSideMenuDelegate) {
	
	$scope.poemSlides = {
		numViewableSlides : 0,
		slideIndex : 0,
		slides : []
	};		

	$scope.poemSlides.slides.push(poem);	
	$scope.poem = poem;
	$scope.list = [];
	$scope.likeClass = 'button button-stable icon ion-bookmark';
	$scope.likedItems = savedLikedItems;
	$scope.currentActiveSlide = 0;
	$ionicSideMenuDelegate.canDragContent(false);
	
	var orderBy = $filter('orderBy');
	var choiceStatus = -1;
	
	$scope.toggleLeft = function() {
		$ionicSideMenuDelegate.toggleLeft();
	};	
	
	nextSlide = function() {
		$ionicSlideBoxDelegate.next();
	};
	
	countSlides = function() {
		$scope.poemSlides.numViewableSlides = 0;
		angular.forEach($scope.poemSlides.slides, function() {
			$scope.poemSlides.numViewableSlides++;
		})
	};		
	
	$scope.slideChanged = function(index) {
		console.log("Index Now: " + index);
		
		if (index == $scope.poemSlides.slides.length) {
			$ionicSlideBoxDelegate.previous();
		}
		
		else {
			changeScope($scope.poemSlides.slides[index]);
			$scope.poemSlides.slideIndex = index - 1;
			$scope.currentActiveSlide = index;
			scrollTop();
		}
	
	};	
	
	$scope.findSimilar = function() {
		choiceStatus = 0;
		var res = $http.post('http://localhost:8080/findSimilar', savedHistory);
		res.success(function(data) {
			savedHistory.push(data.poemID);
			updateSlideBox(data);
		});
		res.error(function(data, status, headers, config) {
			alert( "failure message: " + JSON.stringify({data: data}));
		});
	};
	
	updateSlideBox = function(data) {
		console.log("SLIDE INDEX IS: " + $scope.poemSlides.slideIndex);
		$scope.poemSlides.slides = $scope.poemSlides.slides.slice(0, $scope.currentActiveSlide + 1);
		$scope.poemSlides.slides.push(data); 
		countSlides();
		$ionicSlideBoxDelegate.update();
		$scope.currentActiveSlide = $scope.poemSlides.slides.length - 1;
		$ionicSlideBoxDelegate.next();
		scrollTop();
	}
	
	$scope.openNew = function(item) {
		savedHistory.push(item.poemID);
		choiceStatus = 1;
		savedPopup.close();
		updateSlideBox(item);
	};
	
	changeScope = function(item) {
		changeStar(item.poemID);
		$scope.poem = item;
		poem = item;
	};


	mulligan = function() {
		//$scope.closePopup();
		var res = $http.post('http://localhost:8080/getRandDistPoems', savedHistory);
		temp = [];
		res.success(function(data) {
			for(var i = 0; i < data.length; i++) {
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
	};

	
	
	getNewList = function() {
		var tempHistory = savedHistory;
		tempHistory.push(poem.poemID);
		var res = $http.post('http://localhost:8080/getRandDistPoems', tempHistory);
		$scope.list = [];
		
		if (tempHistory.length > 1)
			tempHistory.pop();
		
		res.success(function(data) {
			for(var i = 0; i < data.length; i++) {
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
		console.log("LIKING: " + id);
		if (checkLiked(id) == -1) {
			
			changeStarToLiked();

			savedBookmarks.push(poem);
			savedLikedItems.push(id);
			$scope.likedItems.push(id);
			
			var likedPoem = {userId: userID, poemId: poem.poemID, weight: poem.distribution, afterSimilar: choiceStatus};
			
			var res = $http.post('http://localhost:8080/likePoem', likedPoem);
			res.success(function(data) {
				console.log("Poem Liked: " + id);
			});	
			res.error(function(data, status, headers, config) {
				alert( "failure while liking poem: " + JSON.stringify({data: data}));
			});	
		};
	};
	
	
	$scope.showPopup = function() {
		getNewList();
		$scope.data = {}
		savedPopup = $ionicPopup.show({
			templateUrl: 'newListPopup.html',
			title: 'Related Poems',
			scope: $scope,
			buttons: [
				{ text: 'Back' },
				{
					text: '<b>Shuffle</b>',
					type: 'button-positive',
					onTap: function(e) 
						{
							e.preventDefault();
							mulligan();
						}
				}
			]
		});
	};
  
	$scope.closePopup = function() {
		savedPopup.close();
	};	
	
	changeStar = function(id) {
		if (checkLiked(id) == -1) {
			changeStarToUnliked();
		} else {
			changeStarToLiked();
		}
	};
	
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
		$ionicScrollDelegate.scrollTop(true);
	};

	countSlides();
	changeStar(poem.poemID);
	getNewList();
})

.controller('HomeController', function($scope, $state, poemList, $ionicHistory) {
	
	$scope.currentIndex = 50;
	$scope.currentList = poemList;
	
	$scope.goToPoem = function(item) {
		$ionicHistory.clearCache();
		savedHistory = [item];
		$state.go('main.poem', {poemId: item});
	};
	
	$scope.showMore = function() {
		$scope.currentIndex += 50;
		if ($scope.currentIndex >= poemList.length)
			$scope.currentIndex = poemList.length;
		$scope.$broadcast('scroll.infiniteScrollComplete');
	};
});


