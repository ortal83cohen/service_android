'use strict';

(function (document, androidConnector) {

    var YOUTUBE_IFRAME = '<iframe src="//www.youtube.com/embed/$1?wmode=transparent" frameborder="0" allowfullscreen></iframe>';

    var SLIDESHARE_IFRAME = '<iframe src="//www.slideshare.net/slideshow/embed_code/$1" frameborder="0" scrolling="no" allowfullscreen></iframe>';

    // Assumes article can be retrieved by JS interface. References ArticleContentWebConnector
    function getContent() {
        return androidConnector.getArticleContent();
    };

    function stripScripts(s) {
        var div = document.createElement('div');
        div.innerHTML = s;
        var scripts = div.getElementsByTagName('script');
        var i = scripts.length;
        while (i--) {
            scripts[i].parentNode.removeChild(scripts[i]);
        }
        return div.innerHTML;
    };

    function replaceWebMedia(content) {
        content = content.replace(/\[[\s,&nbsp;]*web-media[\s,&nbsp;]*:[\s,&nbsp;]*youtube[\s,&nbsp;]*,[\s,&nbsp;]*([\w\-]+)[\s,&nbsp;]*]/g, YOUTUBE_IFRAME);
        content = content.replace(/\[[\s,&nbsp;]*web-media[\s,&nbsp;]*:[\s,&nbsp;]*slideshare[\s,&nbsp;]*,[\s,&nbsp;]*([\w\-]+)[\s,&nbsp;]*]/g, SLIDESHARE_IFRAME);
        return content;
    };

    // Assumes id 'article-content' exists, reference ArticleClientImpl
    function displayContent(content) {
        var articleContentElement = document.getElementById('article-content');
        articleContentElement.innerHTML = content;
    }

    var content = getContent();
    content = stripScripts(content);
    content = replaceWebMedia(content);
    displayContent(content);

})(document, window.androidConnector);