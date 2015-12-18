/*
 * No license defined yet.
 */
angular.module('SiemGui').filter('stripHtml', function() {
	/**
	 * Strip HTML tags from an input string.
	 * Solution is taken from: http://stackoverflow.com/questions/17289448/angularjs-to-output-plain-text-instead-of-html
	 *
	 * @param {String} text Input text for filter
	 * @returns {String} Filtered text
	 */
	return function(text) {
		return String(text).replace(/<[^>]+>/gm, '');
	};
});