(function (global) {
    "use strict";
    function Clock(el) {
        let document = global.document;
        this.el = document.getElementById(el);
        this.months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    }
    Clock.prototype.addZero = function (i) {
        if (i < 10) {
            i = "0" + i;
            return i;
        }
        return i;
    };
    Clock.prototype.updateClock = function () {
        let now, year, month, dayNumber, hour, minute, result, self;
        now = new global.Date();
        year = now.getFullYear();
        month = now.getMonth();
        dayNumber = now.getDate();
        hour = this.addZero(now.getHours());
        minute = this.addZero(now.getMinutes());
        result = this.months[month] + ", " + dayNumber + ", " + year + " год " + hour + ":" + minute;
        self = this;
        self.el.innerHTML = result;
        global.setTimeout(function () {
            self.updateClock();
        }, 7000);
    };
    global.Clock = Clock;
}(window));

function addEvent(elm, evType, fn, useCapture) {
    "use strict";
    if (elm.addEventListener) {
        elm.addEventListener(evType, fn, useCapture);
    } else if (elm.attachEvent) {
        elm.attachEvent('on' + evType, fn);
    } else {
        elm['on' + evType] = fn;
    }
}

addEvent(window, "load", function () {
    if (document.getElementById("clock")) {
        let clock = new Clock("clock");
        clock.updateClock();
    }
});