
var currentRow ;

function editUserServer() {
    var elements = document.getElementById("edit-form").elements;
    var obj ={
    id : $("#modal-id").val(),
    login : $("#modal-login").val(),
    password : $("#modal-pass").val(),
    name : $("#modal-name").val(),
    age : $("#modal-age").val()
    };

  $.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/updateUser",
             data: JSON.stringify(obj),
             cache: false,
             timeout: 600000,
             success: function (id) {
             let new_row = createUserRow($("#modal-id").val(), $("#modal-login").val(), $("#modal-pass").val(), $("#modal-name").val(), $("#modal-age").val());
                          $("table tbody").find($(currentRow)).replaceWith(new_row);
                      document.getElementById('edit-form').reset();
                      $('#modalEditForm').modal('hide');
             },
             error: function (e) {
             alert("User was not saved")
                        }
  });
      return false;


}

function editUser(button) {
    currentRow = $(button).closest('tr');
    $("#modal-id").val($('.user-id', currentRow).text());
    $("#modal-login").val($('.user-login', currentRow).text());
    $("#modal-pass").val($('.user-password', currentRow).text());
    $("#modal-name").val($('.user-name', currentRow).text());
    $("#modal-age").val($('.user-age', currentRow).text());
     $('#modalEditForm').modal('show');
    }

function createUserRow(id ,login, password, name, age) {
  return "<tr>" + "<td class=\"user-id\">" + id + "</td>" + "<td class=\"user-login\">" + login + "</td>" +
             "<td class=\"user-password\">" + password + "</td>" + "<td class=\"user-name\">" + name + "</td>" + "<td class=\"user-age\">" + age + "</td>"
             + "<td><button onclick=\"editUser(this)\">EDIT</button></td>"  + "   " + "<td>" +"<button onclick=\"deleteUser(this)\">Delete</button>" + "</td>" + "</tr>";
}


function getAllUsers()
{   $.getJSON('/api/getAllUsers', function (data) {
$(".users").find("tbody").empty();
       $.each(data, function(i, f) {
        var new_row =  createUserRow(f.id ,f.login, f.password, f.name, f.age)
        $(new_row).appendTo(".users tbody");
     });
        });
  };


function deleteUser(button) {
$.ajax({
           type: "POST",
           contentType: "application/json",
           url: "/api/deleteUser",
           data: JSON.stringify($(button).parent().siblings(":first").text()),
           cache: false,
           timeout: 600000,
           success: function (json) {
           $(button).parents("tr").remove();
           },
           error: function (e) {
           alert("User was not deleted")
                      }
});
}

function saveUser(e) {
 e.preventDefault();
    var elements = document.getElementById("formuser").elements;
    var obj ={};
    for(var i = 0 ; i < elements.length ; i++){
        var item = elements.item(i);
        obj[item.name] = item.value;
    }

  $.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/saveUser",
             data: JSON.stringify(obj),
             cache: false,
             timeout: 600000,
             success: function (id) {
             document.getElementById('formuser').reset();
          var new_row =   createUserRow(JSON.parse(id) ,obj.login, obj.password, obj.name, obj.age);
            $(new_row).appendTo(".users tbody");
             },
             error: function (e) {
             alert("User was not saved")
                        }
  });
      return false;

}
