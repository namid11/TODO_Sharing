

// Modal表示時
$('#editModal').on('show.bs.modal', function(event) {
    console.log("modalが表示されました");
    var sender = event.relatedTarget;                   // イベントの元を取得(JSのDOM)
    const todo_id = sender.getAttribute('data-id');     // インベント元からidを取得
    const attrs = sender.innerText.split('\n');         // イベント元からTODOの属性値を取得. Splitで分割.
    const input_todoContent = $(this).find('#todo-content');  // ModalからInput_text部分を取得
    const input_todoFlag = $(this).find('#complete-check');   // Modalからcheckbox部分を取得
    const input_todoId = $(this).find('#modal-todo-id');
    input_todoContent.val(attrs[0]);  // input_textのvalueに値を設定
    input_todoFlag[0].checked = attrs[1] !== '未完了';     // checkの値を設定
    input_todoId.val(todo_id);
}).on('hide.bs.modal', function(event) {
    //document.getElementById('modal-update-button').setAttribute('disabled');
    $('#modal-update-button').prop('disabled', true);   // UPDATEボタンを選択不可に
    console.log("modalが閉じられました");
});


// 編集Modalで変化があった時に呼ばれるメソッド
var alertValue = (event) => {
    //document.getElementById('modal-update-button').removeAttribute('disabled');
    $('#modal-update-button').prop('disabled', false);  // UPDATEボタンを選択可能に
};