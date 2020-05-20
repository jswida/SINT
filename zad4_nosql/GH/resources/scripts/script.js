'use strict';
const SERVER_URL = 'http://localhost:8000';


function ObservableObject(data) {
    let self = this;
    ko.mapping.fromJS(data, {}, self);

    ko.computed(function() {
        return ko.mapping.toJSON(self);
    }).subscribe(function(res) {
        let resource = ko.mapping.fromJSON(res);
        $.ajax({
            url: resourceUrl(resource),
            type: 'PUT',
            dataType : "json",
            contentType: "application/json",
            data: ko.mapping.toJSON(self)
        }).done(function(data) {
            console.log('Record updated');
        });
    });
}

function removedObjectCallback(changes) {
    changes.forEach(function(change) {
        // Student / Course deleted from database
        if (change.status === 'deleted') {
            $.ajax({
                url: resourceUrl(change.value),
                type: 'DELETE',
                dataType : "json",
                contentType: "application/json"
            }).done(function() {
                console.log('Object removed from service');
            });
        }
    })
}

function resourceUrl(record, type = 'self') {
    const links = record.link();
    const resourceUrl = links.find(function(link) {
        return link.params.rel() === type
    });

    return resourceUrl.href();
}