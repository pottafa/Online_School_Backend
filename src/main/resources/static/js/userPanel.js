
var currentRow ;

function editUserServer() {
    var elements = document.getElementById("edit-form").elements;
    var json = {};
    var user ={
    id : $("#modal-id").val(),
    login : $("#modal-login").val(),
    password : $("#modal-pass").val(),
    };
     var userProfile ={
      name : $("#modal-name").val(),
              age : $("#modal-age").val()
      };
json["user"] = user;
        json["userProfile"] = userProfile;
  $.ajax({
             type: "PUT",
             contentType: "application/json",
             url: "/api/updateUser",
             data: JSON.stringify(json),
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
        let name;
              let age;
       if(f.profile != null) {
       var profile = f.profile;
    if( profile["name"] != null) name = profile["name"];
    if( profile["age"] != null) age = profile["age"];
      }
        var new_row =  createUserRow(f.id ,f.login, f.password, name, age)
        $(new_row).appendTo(".users tbody");
     });
        });
  };


function deleteUser(button) {
$.ajax({
           type: "DELETE",
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
    var json ={};
    var user ={};

    let role = {};
    role.name = $('#Id :selected').val();
    role.id = $("#roles")[0].selectedIndex;
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
json["user"] = user;
        json["userProfile"] = userProfile;

  $.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/saveUser",
             data: JSON.stringify(json),
             cache: false,
             timeout: 600000,
             success: function (id) {
             document.getElementById('formuser').reset();
          var new_row =   createUserRow(JSON.parse(id) ,user.login, user.password, userProfile.name, userProfile.age);
            $(new_row).appendTo(".users tbody");
             },
             error: function (e) {
             alert("User was not saved")
                        }
  });
      return false;

}

function showCourses()
{   $.getJSON('/api/getAllCourses', function (data) {
$(".courses").find("tbody").empty();
       $.each(data, function(i, f) {
        var new_row =  createCourseRow(f.title, (f.groups).length, f.description)
        $(new_row).appendTo(".courses tbody");
     });
        });
  };

  function createCourseRow(title ,numGroups, description) {
    return "<tr>" + "<td class=\"course-title\">" + title + "</td>" +
               "<td class=\"course-groups\">" + numGroups + "</td>" + "<td class=\"course-description\">" +
                 "<textarea name=\"description\" rows=\"5\" cols=\"30\">" + description + "</textarea> </td>" +
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
             url: "/api/createCourse",
             data: JSON.stringify(obj),
             cache: false,
             timeout: 600000,
             success: function (id) {
             document.getElementById('formCourse').reset();
          var new_row =   createCourseRow(obj.title, 0 , obj.description);
            $(new_row).appendTo(".courses tbody");
             },
             error: function (e) {
             alert("Course was not created")
                        }
  });
      return false;

}

function deleteCourse(button) {
$.ajax({
           type: "DELETE",
           contentType: "application/json",
           url: "/api/deleteCourse",
           data: JSON.stringify($(button).parent().siblings(":first").text()),
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
var title = $(button).parent().siblings(":first").text();
window.location.href = "courses/"+title;

   return true;
}