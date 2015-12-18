/*
 * No license defined yet.
 */
angular.module('SiemGui').filter('stripLineBreaks', function() {
	/**
	 * Strip line break symbols from an input string.
	 *
	 * @param {String} text Input text for filter
	 * @returns {String} Filtered text
	 */
	return function(text) {
		return String(text).replace(/[\n\r]/gm, '');
	};
});