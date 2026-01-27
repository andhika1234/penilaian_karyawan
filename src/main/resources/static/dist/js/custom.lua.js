function goBack() {
    window.history.back();
}

function rupiahInput(nilai){
    var items=accounting.formatMoney(parseInt(nilai), "", 0, ".", ",")
    return items;
}