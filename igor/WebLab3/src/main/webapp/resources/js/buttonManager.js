//Изменение выбранного бокса для X
function changeSelectedX(newValue) {
    $('.x_value').val(newValue).change();
}
function changeInputY(y) {
    rValue = $('.r_value').val();
    let newYValue = ((y - 150) / -100) * rValue;
    $('.y_value').val(Number(newYValue.toFixed(2)));
}
function getSelectedX(x) {
    rValue = $('.r_value').val();
    let newYValue = ((x - 150) / 100) * rValue;
    let valueWithLowerStep;
    if (newYValue > 2) {
        valueWithLowerStep = "2.0"
    } else {
        if (newYValue < -2) {
            valueWithLowerStep = "-2.0"
        } else {valueWithLowerStep = (Math.round(newYValue * 2) / 2).toFixed(1);}
    }
    switch (valueWithLowerStep) {
        case "-2.0":
            changeSelectedX("-2");
            break;
        case "-1.5":
            changeSelectedX("-1.5");
            break;
        case "-1.0":
            changeSelectedX("-1");
            break;
        case "-0.5":
            changeSelectedX("-0.5");
            break;
        case "-0":
            changeSelectedX("-0");
            break;
        case "0.5":
            changeSelectedX("0.5");
            break;
        case "1.0":
            changeSelectedX("1");
            break;
        case "1.5":
            changeSelectedX("1.5");
            break;
        case "2.0":
            changeSelectedX("2");
            break;
    }
}

function checkOnSelectedR() {
    if (rValue && rValue >= 2 && rValue <= 5) {
        return true;
    } else {
        document.querySelector('.validation_message').innerHTML = "<span>Не выбрано или неверно задано значение R</span><br>"
        return false;
    }
}