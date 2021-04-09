$(function() {
    var tabButtons = $(".tabs li");
    var tabs = $("#tabs>div");

    tabButtons.on('click', function(event) {
        var activeIndex = $(this).index();

        tabButtons.each(function(index, value, array) {
            if (index === activeIndex) {
                $(value).addClass("is-active");
            }
            else {
                $(value).removeClass("is-active");
            }
        });

        tabs.each(function(index, value, array) {
            if (index === activeIndex) {
                $(value).removeClass("is-hidden");
            }
            else {
                $(value).addClass("is-hidden");
            }
        });

    });

    $("#test-button").on('click', function(event) {
        document.getElementById('test-viewer').editor.setValue('');
        $("#parameters input").each(function(index, value) {
            value.value = '';
        })
    })
});

function go(form, editor) {

    var button = $(form).find('button[type=submit]');

    var regex = /\{(.+?)}/g;
    var template = form.action;
    var match = regex.exec(template);
    while (match != null) {
        var name = match[1];
        var value = $(form).find('input[name=' + name + ']').val();
        template = template.replace(match[0], value);
        match = regex.exec(template);
    }

    button.addClass('is-loading');

    fetch(template, {
            //mode: 'no-cors',
            method: form.method,
            headers: new Headers({
                "X-Requested-With": "Wexy"
            })
        })
        .then(function(response) {
            // TODO handle other types
            var header = response.headers.get("Content-Type");
            var contentType = header == null ? "" : header.toLowerCase();
            if (contentType.includes("json")) {
                response.json().then(function (json) {
                    var jsonString = JSON.stringify(json, null, 4);
                    editor.session.setMode("ace/mode/json");
                    editor.setValue(jsonString);
                });
            }
            if (contentType.includes("xml")) {
                response.text().then(function (xml) {
                    editor.session.setMode("ace/mode/xml");
                    editor.setValue(xml);
                });
            }
            else {
                response.text().then(function (text) {
                    editor.session.setMode("ace/mode/text");
                    editor.setValue(text);
                });
            }
        })
        .then(function (json) {
            button.removeClass('is-loading');
        })
        .catch(function (error) {
            console.log('Request failed', error);
            button.removeClass('is-loading');
            editor.setValue('');

            bulmaToast.toast({
                message: "Request failed",
                type: "is-danger",
                animate: { in: 'fadeIn', out: 'fadeOut' }
            });
        });

}

function makeEditor(element, type) {
    var editor = editify(element, type);
    editor.setOptions({
        autoScrollEditorIntoView: true,
        maxLines: 10
    });

    var input = $(element).next();
    editor.getSession().on('change', function() {
        input.val(editor.getSession().getValue());
    });
}

function makeViewer(element, type) {
    var editor = editify(element, type);
    editor.setOptions({
        autoScrollEditorIntoView: true,
        maxLines: 10,
        readOnly: true,
        highlightActiveLine: false,
        highlightGutterLine: false
    });
}

function editify(element, type) {
    var editor = ace.edit(element.id);
    editor.setTheme("ace/theme/chrome");
    editor.session.setMode("ace/mode/" + type)
    editor.setShowPrintMargin(false);
    editor.renderer.setScrollMargin(10, 10);

    element.editor = editor;
    return editor;
}