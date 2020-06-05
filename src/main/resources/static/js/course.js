var currentRow ;

function editLessonServer() {
    var json = {};
        var lesson ={};
        lesson["id"] = $("#modal-id").val();
        lesson["title"] = $("#modal-title").val();
        lesson["description"] = $("#modal-description").val();

        json["lesson"] = lesson;
        json["courseTitle"] = title;

  $.ajax({
             type: "PUT",
             contentType: "application/json",
             url: "/api/updateLesson",
             data: JSON.stringify(json),
             cache: false,
             timeout: 600000,
             success: function (id) {
             let new_row = createLessonRow($("#modal-title").val(), $("#modal-description").val(), $("#modal-id").val());
                          $("table tbody").find($(currentRow)).replaceWith(new_row);
                      document.getElementById('edit-form').reset();
                      $('#modalLessonEditForm').modal('hide');
             },
             error: function (e) {
             alert("Lesson was not updated")
                        }
  });
      return false;
}


function editLesson(button) {
    currentRow = $(button).closest('tr');
    $("#modal-id").val($('.lesson-id', currentRow).text());
    $("#modal-title").val($('.lesson-title', currentRow).text());
    $("#modal-description").val($('.lesson-description', currentRow).text());
     $('#modalLessonEditForm').modal('show');
    }

function showLessons() {
$.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/getCourseLessons",
             data: JSON.stringify(title),
             cache: false,
             timeout: 600000,
             success: function (data) {
             $(".lessons").find("tbody").empty();
                    $.each(data, function(i, f) {
                     var new_row =  createLessonRow(f.title, f.description, f.id)
                     $(new_row).appendTo(".lessons tbody");
                  });
             },
  });
}


  function createLessonRow(title , description, id) {
    return "<tr>" + "<td sec:authorize=\"hasRole('ROLE_ADMIN')\" class=\"lesson-id\">" + id + "</td>" + "<td class=\"lesson-title\">" + title + "</td>" +
                "<td class=\"lesson-description\">" + "<textarea name=\"description\" rows=\"5\" cols=\"30\">" + description + "</textarea> </td>" +
                  "<td><button onclick=\"editLesson(this)\">EDIT</button></td>"  + "   " + "<td>" +"<button onclick=\"deleteLesson(this)\">Delete</button>" + "</td>" + "</tr>";
  }

function saveLesson(e) {
 e.preventDefault();
 var json = {};
    var elements = document.getElementById("formlesson").elements;
    var lesson ={};
    for(var i = 0 ; i < elements.length ; i++){
        var item = elements.item(i);
        lesson[item.name] = item.value;
    }
    json["lesson"] = lesson;
    json["courseTitle"] = title;
  $.ajax({
             type: "POST",
             contentType: "application/json",
             url: "/api/createLesson",
             data: JSON.stringify(json),
             cache: false,
             timeout: 600000,
             success: function (data) {
             document.getElementById('formlesson').reset();
          var new_row =   createLessonRow(lesson.title, lesson.description, JSON.parse(data));
            $(new_row).appendTo(".lessons tbody");
             },
             error: function (e) {
             alert("Lesson was not created because: " + e)
                        }
  });
      return false;

}

function deleteLesson(button) {
let json = {};
let lesson = {};
lesson["id"] = $(button).parent().siblings(".lesson-id").text();
lesson["title"] = $(button).parent().siblings(".lesson-title").text();
lesson["description"] = $(button).parent().siblings(".lesson-description").text().trim();
 json["lesson"] = lesson;
    json["courseTitle"] = title;
$.ajax({
           type: "DELETE",
           contentType: "application/json",
           url: "/api/deleteLesson",
           data: JSON.stringify(json),
           cache: false,
           timeout: 600000,
           success: function (json) {
           $(button).parents("tr").remove();
           },
           error: function (e) {
           alert("Lesson was not deleted because: " + e)
                      }
});
}