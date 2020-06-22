$(document).ready(function(){


var currentRow;


//# ===============================
//#        Datatable
//# ===============================

  courseTableConfig = {
        "columns": [
                            { "data": "id", "sClass": "course_id"  },
                            { "data": "title" , "sClass": "course_title"  },
                            { "data": "description", "sClass": "course_description"  },
                            { "data": "functions",      "sClass": "functions" ,
                              "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                          "<li class=\"function_edit\"><a><span>Edit</span></a></li>" +
                                                         "<li class=\"function_delete\"><a><span>Delete</span></a></li>" +
                                                          "</ul></div>"},
                              { "data": "page",      "sClass": "functions" ,
                               "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                           "<li class=\"function_course_page\"><a><span>Page</span></a></li>" + "</ul></div>"}
                        ],
                        "aoColumnDefs": [
                              { "bSortable": false, "aTargets": [-1] }
                            ],
                            "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
                            "oLanguage": {
                              "oPaginate": {
                                "sFirst":       " ",
                                "sPrevious":    " ",
                                "sNext":        " ",
                                "sLast":        " ",
                              },
                              "sLengthMenu":    "Records per page: _MENU_",
                              "sInfo":          "Total of _TOTAL_ records (showing _START_ to _END_)",
                              "sInfoFiltered":  "(filtered from _MAX_ total records)"
                            }
      };

    var table_courses = $('#table_courses').DataTable(courseTableConfig);

    $('#load_courses').on('click', function() {
        courseTableConfig.ajax = {
          "url": "/api/courses",
                      "type": "GET",
                      "dataSrc": "",
        };

        table_courses.destroy();
        table_courses = $('#table_courses').DataTable( courseTableConfig );
      });

  
  //# ===============================
  //#        Validator
  //# ===============================

  jQuery.validator.setDefaults({
    success: 'valid',
    rules: {
    title: {
        required: true,
        minlength: 5,
        maxlength: 20
      },
    },
    errorPlacement: function(error, element){
      error.insertBefore(element);
    },
    highlight: function(element){
      $(element).parent('.field_container').removeClass('valid').addClass('error');
    },
    unhighlight: function(element){
      $(element).parent('.field_container').addClass('valid').removeClass('error');
    }
  });
  var form_course = $('#form_course');
  form_course.validate();

  //# ===============================
  //#        Messages/Modals
  //# ===============================

  // Show message
  function show_message(message_text, message_type){
    $('#message').html('<p>' + message_text + '</p>').attr('class', message_type);
    $('#message_container').show();
    if (typeof timeout_message !== 'undefined'){
      window.clearTimeout(timeout_message);
    }
    timeout_message = setTimeout(function(){
      hide_message();
    }, 8000);
  }

  // Hide message
  function hide_message(){
    $('#message').html('').attr('class', '');
    $('#message_container').hide();
  }

  // Show lightbox
  function show_lightbox(){
    $('.lightbox_bg').show();
    $('.lightbox_container').show();
  }
  // Hide lightbox
  function hide_lightbox(){
    $('.lightbox_bg').hide();
    $('.lightbox_container').hide();
  }

  // Lightbox background
  $(document).on('click', '.lightbox_bg', function(){
    hide_lightbox();
  });

  // Lightbox close button
  $(document).on('click', '.lightbox_close', function(){
    hide_lightbox();
  });

//# ===============================
//#        CREATE COURSE
//# ===============================

  // Add course button
  $(document).on('click', '#add_course', function(e){
    e.preventDefault();
    currentRow = $(this).closest('tr');
    $('.lightbox_content h2').text('Add course');
    $('#form_course button').text('Add course');
    $('#form_course').attr('class', 'form add');
    $('#form_course').attr('data-id', '');
    $('#form_course .field_container label.error').hide();
    $('#form_course .field_container').removeClass('valid').removeClass('error');
    $('#form_course #title').val('');
    $('#form_course #description').val('');
    show_lightbox();
  });

  // Add course submit form
  $(document).on('submit', '#form_course.add', function(e){
    e.preventDefault();
    // Validate form
    if (form_course.valid() == true){
      // Get data from course form
      hide_lightbox();
      var course = {};
      const form_data = new FormData(document.querySelector('#form_course'));
      form_data.forEach(function(value, key){
          course[key] = value;
      });
      // Send course information to database
      var request   = $.ajax({
        url:          '/api/courses',
        cache:        false,
        data:         JSON.stringify(course),
        contentType: "application/json",
        type:         'POST',
        success: function (id) {
                 show_message("Course '" + title + "' added successfully.", 'success');
                 course.id = id;
                           table_courses.row.add(course).draw();
                     },
        error: function (e) {
        show_message('Add request failed: '+ JSON.parse(e), 'error');
 }
      });
    }
  });

//# ===============================
//#        EDIT course
//# ===============================

  // Edit course button
  $(document).on('click', '.function_edit a', function(e){
    e.preventDefault();
    var id      = $(this).closest('tr').find('.course_id').text();
     currentRow = $(this).closest('tr');
        $('.lightbox_content h2').text('Edit course');
        $('#form_course button').text('Edit course');
        $('#form_course').attr('class', 'form edit');
        $('#form_course').attr('data-id', id);
        $('#form_course .field_container label.error').hide();
        $('#form_course .field_container').removeClass('valid').removeClass('error');
        $('#form_course #title').val($('.course_title', currentRow).text());
        $('#form_course #description').val($('.course_description', currentRow).text());
        show_lightbox();
  });
  
  // Edit course submit form
  $(document).on('submit', '#form_course.edit', function(e){
    e.preventDefault();
    // Validate form
    if (form_course.valid() == true){
      // Send course information to server
      hide_lightbox();
      var id        = $('#form_course').attr('data-id');
      var course = {};
            const form_data = new FormData(document.querySelector('#form_course'));
            form_data.forEach(function(value, key){
                course[key] = value;
            });
            course.id = id;

      var request   = $.ajax({
        url:          '/api/courses/' + id,
        cache:        false,
        data:         JSON.stringify(course),
        contentType: "application/json",
        type:         'PUT',
        success: function (course) {
         show_message("course '" + course.title + "' updated successfully.", 'success');
                                   table_courses.row(currentRow).data(course).draw();
                             },
                error: function (e) {
                show_message('Update request failed: '+ JSON.parse(e), 'error');
         }
      });
    }
  });

  //# ===============================
  //#        DELETE course
  //# ===============================
  
  // Delete course
  $(document).on('click', '#table_courses .function_delete a', function(e){
    e.preventDefault();
    var row = $(this).closest('tr');
    var title =  row.find('.course_title').text();
    if (confirm("Are you sure you want to delete '" + title + "'?")){
      var id      = row.find('.course_id').text();
      // Send delete request
      var request = $.ajax({
        url:          '/api/courses/' + id,
        type:         'DELETE',
        success: function () {
        show_message("course '" + title + "' successfully deleted", 'success');
                         table_courses.row(row).remove().draw();
                             },
                error: function (e) {
                show_message('Delete request failed: ' + JSON.parse(e), 'error');
         }
      });
    }
  });


 $(document).on('click', '#refresh_courses', function(e){
 $('#table_courses').DataTable().ajax.reload();
 });



 $(document).on('click', '#table_courses .function_course_page a', function(e){
  var row = $(this).closest('tr');
      var id =  row.find('.course_id').text();
  window.location.href = "courses/"+id;
     return true;
 });

});
