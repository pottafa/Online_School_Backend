$(document).ready(function(){


var currentRow;


//# ===============================
//#        Datatable
//# ===============================

  lessonTableConfig = {
        "columns": [
                            { "data": "id", "sClass": "lesson_id"  },
                            { "data": "title" , "sClass": "lesson_title"  },
                            { "data": "description", "sClass": "lesson_description"  },
                            { "data": "functions",      "sClass": "functions" ,
                              "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                          "<li class=\"function_edit\"><a><span>Edit</span></a></li>" +
                                                         "<li class=\"function_delete\"><a><span>Delete</span></a></li>" +
                                                          "</ul></div>"}
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

    var table_lessons = $('#table_lessons').DataTable(lessonTableConfig);

    $('#load_lessons').on('click', function() {
        lessonTableConfig.ajax = {
          "url": "/api/courses/" + course_id + "/lessons",
                      "type": "GET",
                      "dataSrc": "",
        };

        table_lessons.destroy();
        table_lessons = $('#table_lessons').DataTable( lessonTableConfig );
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
  var form_lesson = $('#form_lesson');
  form_lesson.validate();

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
//#        CREATE LESSON
//# ===============================

  // Add lesson button
  $(document).on('click', '#add_lesson', function(e){
    e.preventDefault();
    currentRow = $(this).closest('tr');
    $('.lightbox_content h2').text('Add lesson');
    $('#form_lesson button').text('Add lesson');
    $('#form_lesson').attr('class', 'form add');
    $('#form_lesson').attr('data-id', '');
    $('#form_lesson .field_container label.error').hide();
    $('#form_lesson .field_container').removeClass('valid').removeClass('error');
    $('#form_lesson #title').val('');
    $('#form_lesson #description').val('');
    show_lightbox();
  });

  // Add lesson submit form
  $(document).on('submit', '#form_lesson.add', function(e){
    e.preventDefault();
    // Validate form
    if (form_lesson.valid() == true){
      // Get data from lesson form
      hide_lightbox();
      var lesson = {};
      const form_data = new FormData(document.querySelector('#form_lesson'));
      form_data.forEach(function(value, key){
          lesson[key] = value;
      });
      // Send lesson information to database
      var request   = $.ajax({
        url:          '/api/courses/' + course_id + '/lessons',
        cache:        false,
        data:         JSON.stringify(lesson),
        contentType: "application/json",
        type:         'POST',
        success: function (id) {
                 show_message("Lesson '" + lesson.title + "' added successfully.", 'success');
                 lesson.id = id;
                           table_lessons.row.add(lesson).draw();
                     },
        error: function (e) {
        show_message('Add request failed: '+ JSON.parse(e), 'error');
 }
      });
    }
  });

//# ===============================
//#        EDIT lesson
//# ===============================

  // Edit lesson button
  $(document).on('click', '#table_lessons .function_edit a', function(e){
    e.preventDefault();
    var id      = $(this).closest('tr').find('.lesson_id').text();
     currentRow = $(this).closest('tr');
        $('.lightbox_content h2').text('Edit lesson');
        $('#form_lesson button').text('Edit lesson');
        $('#form_lesson').attr('class', 'form edit');
        $('#form_lesson').attr('data-id', id);
        $('#form_lesson .field_container label.error').hide();
        $('#form_lesson .field_container').removeClass('valid').removeClass('error');
        $('#form_lesson #title').val($('.lesson_title', currentRow).text());
        $('#form_lesson #description').val($('.lesson_description', currentRow).text());
        show_lightbox();
  });
  
  // Edit lesson submit form
  $(document).on('submit', '#form_lesson.edit', function(e){
    e.preventDefault();
    // Validate form
    if (form_lesson.valid() == true){
      // Send lesson information to server
      hide_lightbox();
      var lesson_id        = $('#form_lesson').attr('data-id');
      var lesson = {};
            const form_data = new FormData(document.querySelector('#form_lesson'));
            form_data.forEach(function(value, key){
                lesson[key] = value;
            });
            lesson.id = lesson_id;

      var request   = $.ajax({
        url:          '/api/courses/' + course_id + '/lessons/' + lesson_id,
        cache:        false,
        data:         JSON.stringify(lesson),
        contentType: "application/json",
        type:         'PUT',
        success: function (lesson) {
         show_message("Lesson '" + lesson.title + "' updated successfully.", 'success');
                                   table_lessons.row(currentRow).data(lesson).draw();
                             },
                error: function (e) {
                show_message('Update request failed: '+ JSON.parse(e), 'error');
         }
      });
    }
  });

  //# ===============================
  //#        DELETE lesson
  //# ===============================
  
  // Delete lesson
  $(document).on('click', '#table_lessons .function_delete a', function(e){
    e.preventDefault();
    var row = $(this).closest('tr');
    var title =  row.find('.lesson_title').text();
    if (confirm("Are you sure you want to delete '" + title + "'?")){
      var lesson_id = row.find('.lesson_id').text();
      // Send delete request
      var request = $.ajax({
        url:          '/api/courses/' + course_id + '/lessons/' + lesson_id,
        type:         'DELETE',
        success: function () {
        show_message("Lesson '" + title + "' successfully deleted", 'success');
                         table_lessons.row(row).remove().draw();
                             },
                error: function (e) {
                show_message('Delete request failed: ' + JSON.parse(e), 'error');
         }
      });
    }
  });


 $(document).on('click', '#refresh_lessons', function(e){
 $('#table_lessons').DataTable().ajax.reload();
 });


});
