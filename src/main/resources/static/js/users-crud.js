$(document).ready(function(){


var currentRow;


//# ===============================
//#        Datatable
//# ===============================
userTableConfig = {
      "columns": [
                          { "data": "id", "sClass": "user_id"  },
                          { "data": "login" , "sClass": "user_login"  },
                          { "data": "email", "sClass": "user_email" ,
                                                                                "defaultContent": ""},
                           { "data": "roles", "sClass": "roles" ,
                                                      "defaultContent": ""},
                          { "data": "functions",      "sClass": "functions" ,
                            "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                        "<li class=\"function_edit\"><a><span>Edit</span></a></li>" +
                                                       "<li class=\"function_delete\"><a><span>Delete</span></a></li>" +
                                                       "<li class=\"function_send_notification\"><a><span>Send notification</span></a></li>" +
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

  var table_users = $('#table_users').DataTable(userTableConfig);

  $('#load_users').on('click', function() {
      userTableConfig.ajax = {
        "url": "/api/users",
                    "type": "GET",
                    "dataSrc": "",
      };

      table_users.destroy();
      table_users = $('#table_users').DataTable( userTableConfig );
    });
  
  //# ===============================
  //#        Validator
  //# ===============================

  jQuery.validator.setDefaults({
    success: 'valid',
    rules: {
    login: {
        required: true,
        minlength: 5,
        maxlength: 12
      },
      password: {
              required: true,
              minlength: 5,
              maxlength: 80
            },
      email: {
        email: true
      },
      age: {
              digits: true
            }
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
  var form_user = $('#form_user');
  form_user.validate();

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
    function show_lightbox(param){
      $('.lightbox_bg').show();
      if (param === 'users') {
      $('.lightbox_container.users').show();
      };
      if (param === 'notification') {
          $('.lightbox_container.notification').show();
          };
    }
    // Hide lightbox
    function hide_lightbox(param){
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
//#        CREATE USER
//# ===============================

  // Add user button
  $(document).on('click', '#add_user', function(e){
    e.preventDefault();
    currentRow = $(this).closest('tr');
    $('.lightbox_content h2').text('Add user');
    $('#form_user button').text('Add user');
    $('#form_user').attr('class', 'form add');
    $('#form_user').attr('data-id', '');
    $('#form_user .field_container label.error').hide();
    $('#form_user .field_container').removeClass('valid').removeClass('error');
    $('#form_user #login').val('');
    $('#form_user .input_container.password').show();
    $('#form_user #password').val('');
    $('#form_user #email').val('');
    show_lightbox('users');
  });

  // Add user submit form
  $(document).on('submit', '#form_user.add', function(e){
    e.preventDefault();
    // Validate form
    if (form_user.valid() == true){
      // Get data from user form
      hide_lightbox();
      var user = {};
      const form_data = new FormData(document.querySelector('#form_user'));
      form_data.forEach(function(value, key){
          user[key] = value;
      });
      // Send user information to database
      var request   = $.ajax({
        url:          '/api/users',
        cache:        false,
        data:         JSON.stringify(user),
        contentType: "application/json",
        type:         'POST',
        success: function (id) {
                 show_message("User '" + user.login + "' added successfully.", 'success');
                 user.id = id;
                           table_users.row.add(user).draw();
                     },
        error: function (e) {
        show_message('Add request failed: '+ JSON.parse(e), 'error');
 }
      });
    }
  });

//# ===============================
//#        EDIT USER
//# ===============================

  // Edit user button
  $(document).on('click', '.function_edit a', function(e){
    e.preventDefault();
    var id      = $(this).closest('tr').find('.user_id').text();
     currentRow = $(this).closest('tr');
        $('.lightbox_content h2').text('Edit user');
        $('#form_user button').text('Edit user');
        $('#form_user').attr('class', 'form edit');
        $('#form_user').attr('data-id', id);
        $('#form_user .field_container label.error').hide();
        $('#form_user .field_container').removeClass('valid').removeClass('error');
        $('#form_user #login').val($('.user_login', currentRow).text());
        $('#form_user .input_container.password').hide();
        $('#form_user #email').val($('.user_email', currentRow).text());
        show_lightbox('users');
  });
  
  // Edit user submit form
  $(document).on('submit', '#form_user.edit', function(e){
    e.preventDefault();
    // Validate form
    if (form_user.valid() == true){
      // Send user information to server
      hide_lightbox();
      var id        = $('#form_user').attr('data-id');
      var user = {};
            const form_data = new FormData(document.querySelector('#form_user'));
            form_data.forEach(function(value, key){
                user[key] = value;
            });
            user.id = id;

      var request   = $.ajax({
        url:          '/api/users/' + id,
        cache:        false,
        data:         JSON.stringify(user),
        contentType: "application/json",
        type:         'PUT',
        success: function (user) {
         show_message("User '" + user.login + "' updated successfully.", 'success');
                                   table_users.row(currentRow).data(user).draw();
                             },
                error: function (e) {
                show_message('Update request failed: '+ JSON.parse(e), 'error');
         }
      });
    }
  });

  //# ===============================
  //#        DELETE USER
  //# ===============================
  
  // Delete user
  $(document).on('click', '#table_users .function_delete a', function(e){
    e.preventDefault();
    var row = $(this).closest('tr');
    var login =  row.find('.user_login').text();
    if (confirm("Are you sure you want to delete '" + login + "'?")){
      var id      = row.find('.user_id').text();
      // Send delete request
      var request = $.ajax({
        url:          '/api/users/' + id,
        type:         'DELETE',
        success: function () {
        show_message("User '" + login + "' successfully deleted", 'success');
                         table_users.row(row).remove().draw();
                             },
                error: function (e) {
                show_message('Delete request failed: ' + JSON.parse(e), 'error');
         }
      });
    }
  });




  //# ===============================
  //#        SEND NOTIFICATION TO USER
  //# ===============================

   // Notification user button
    $(document).on('click', '.function_send_notification a', function(e){
      e.preventDefault();
     var id = $(this).closest('tr').find('.user_id').text();
       currentRow = $(this).closest('tr');
          $('#form_notification').attr('data-id', id);
          $('#form_notification .field_container label.error').hide();
          $('#form_notification .field_container').removeClass('valid').removeClass('error');
          $('#form_notification #notification_subject').val('');
          $('#form_notification #notification_message').val('');
          show_lightbox('notification');
    });

    // Create user notification submit form
    $(document).on('submit', '#form_notification', function(e){
      e.preventDefault();
      // Validate form
        // Send user information to server
        hide_lightbox();
        var id  = $('#form_notification').attr('data-id');
        var notification = {};
              const form_data = new FormData(document.querySelector('#form_notification'));
              form_data.forEach(function(value, key){
                  notification[key] = value;
              });

        var request   = $.ajax({
          url:          '/api/users/' + id + '/notifications',
          cache:        false,
          data:         JSON.stringify(notification),
          contentType: "application/json",
          type:         'POST',
          success: function () {
           show_message("Notification was successfully sent to user " + $(this).closest('tr').find('.user_login').text() , 'success');
                                     table_users.row(currentRow).data(user).draw();
                               },
                  error: function (e) {
                  show_message('Notification request failed: '+ JSON.parse(e), 'error');
           }
        });
    });



});


$(document).on('click', '#refresh_users', function(e){
$('#table_users').DataTable().ajax.reload();
});

