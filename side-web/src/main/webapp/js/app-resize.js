document.addEventListener('DOMContentLoaded', function(event) {

	'use strict';

	// Initialise resize library
	var resize = new window.resize();
	resize.init();

	// Upload photo
	var upload = function(photo, callback) {
		var formData = new FormData();
		formData.append('photo', photo);
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			if (request.readyState === 4) {
				callback(request.response);
			}
		}
		request.open('POST');
		request.responseType = 'json';
		request.send(formData);
	};

	var fileSize = function(size) {
		var i = Math.floor(Math.log(size) / Math.log(1024));
		return (size / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + [ 'B', 'kB', 'MB', 'GB', 'TB' ][i];
	};

	document.querySelector('form input[type=file]').addEventListener('change', function(event) {
		event.preventDefault();

		var files = event.target.files;
		for ( var i in files) {

			if (typeof files[i] !== 'object')
				return false;

		}

	});

});
