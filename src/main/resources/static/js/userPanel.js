
var currentRow ;

function editUserServer() {
    var elements = document.getElementById("edit-form").elements;
    var user ={
    id : $("#modal-id").val(),
    login : $("#modal-login").val(),
    password : $("#modal-pass").val(),
    };
     var userProfile ={
      name : $("#modal-name").val(),
              age : $("#modal-age").val()
      };
        user["userProfile"] = userProfile;
  $.ajax({
             type: "PUT",
             contentType: "application/json",
             url: "/api/users/"+ $("#modal-id").val(),
             data: JSON.stringify(user),
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

function createUserRow(id ,login, password, name, age, roles) {

  return "<tr>" + "<td class=\"user-id\">" + id + "</td>" + "<td class=\"user-login\">" + login + "</td>" +
             "<td class=\"user-password\">" + password + "</td>" + "<td class=\"user-roles\">" + roles + "</td>" + "<td class=\"user-name\">" + name + "</td>" + "<td class=\"user-age\">" + age + "</td>"
             + "<td><button onclick=\"editUser(this)\">EDIT</button></td>"  + "   " + "<td>" +"<button onclick=\"deleteUser(this)\">Delete</button>" + "</td>" + "</tr>";
}


function getAllUsers()
{   $.getJSON('/api/users', function (data) {
$(".users").find("tbody").empty();
       $.each(data, function(i, f) {
        let name;
              let age;
       if(f.profile != null) {
       var profile = f.profile;
    if( profile["name"] != null) name = profile["name"];
    if( profile["age"] != null) age = profile["age"];
      }
       var table_roles = "";
                            for(var i=0; i<(f.roles).length; ++i){
                                table_roles = table_roles + (f.roles)[i].name + "<br>";
                                }
        var new_row =  createUserRow(f.id ,f.login, f.password, name, age, table_roles)
        $(new_row).appendTo(".users tbody");
     });
        });
  };


function deleteUser(button) {
let id = $(button).parent().siblings(":first").text();
$.ajax({
           type: "DELETE",
           contentType: "application/json",
           url: "/api/users/" + id,
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
    let elements = document.getElementById("formuser").elements;
    let user ={};
    let role = {};
    var roles_selection = document.getElementById("roles");
    role_name = roles_selection.options[roles_selection.selectedIndex].text;
    role.id = roles_selection.options[roles_selection.selectedIndex].value;
     user.roles = [role];

    var userProfile ={};
    for(var i = 0 ; i < elements.length -3 ; i++){
        var item = elements.item(i);
        user[item.name] = item.value;
    }
    for(var i = 2 ; i < elements.length ; i++){
            var item = elements.item(i);
            userProfile[item.name] = item.value;
        }
        user["userProfile"] = userProfile;

 $.ajax({
               type: "POST",
               contentType: "application/json",
               url: "/api/users",
               data: JSON.stringify(user),
               cache: false,
               timeout: 600000,
               success: function (id) {
               document.getElementById('formuser').reset();
                           var new_row = createUserRow(id ,user.login, user.password, userProfile.name, userProfile.age,  role_name);
                             $(new_row).appendTo(".users tbody");
               },
               error: function (e) {
               alert(e);
               return false;
                          }
    });
      return false;
}

function showCourses()
{   $.getJSON('/api/courses', function (data) {
$(".courses").find("tbody").empty();
       $.each(data, function(i, f) {
        var new_row =  createCourseRow(f.title, f.id)
        $(new_row).appendTo(".courses tbody");
     });
        });
  };

  function createCourseRow(title, id) {
    return "<tr>" + "<td class=\"course-id\">" + id + "</td>" + "<td class=\"course-title\">" + title + "</td>" +
                  "<td><button onclick=\"return coursePage(this)\">Course page</button></td>"  + "   " + "<td>" +"<button onclick=\"deleteCourse(this)\">Delete</button>" + "</td>" + "</tr>";
  }


function createCourse(e) {
 e.preventDefault();
    var elements = document.getElementById("formCourse").elements;
    var obj ={};
    for(var i = 0 ; i < elements.length ; i++){
        var item = elements.item(i);
        obj[item.name] = item.value;
    }
  $.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/courses",
             data: JSON.stringify(obj),
             cache: false,
             timeout: 600000,
             success: function (id) {
             document.getElementById('formCourse').reset();
          var new_row =   createCourseRow(obj.title, 0 , obj.description, JSON.parse(id));
            $(new_row).appendTo(".courses tbody");
             },
             error: function (e) {
             alert("Course was not created")
                        }
  });
      return false;

}

function deleteCourse(button) {
let courseId = $(button).parent().siblings(":first").text();
$.ajax({
           type: "DELETE",
           contentType: "application/json",
           url: "/api/courses/" + courseId,
           cache: false,
           timeout: 600000,
           success: function (json) {
           $(button).parents("tr").remove();
           },
           error: function (e) {
           alert("Course was not deleted")
                      }
});
}

function coursePage(button) {
var title = $(button).parent().siblings(":nth-child(2)").text();
window.location.href = "courses/"+title;

   return true;
}