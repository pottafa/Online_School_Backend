$(document).ready(function(){


var currentRow;


//# ===============================
//#        Datatable
//# ===============================

  groupTableConfig = {
        "columns": [
                            { "data": "id", "sClass": "group_id"  },
                            { "data": "title" , "sClass": "group_title"  },
                            { "data": "usersCount", "sClass": "group_users_count",
                              "defaultContent": ""},
                            { "data": "functions",      "sClass": "functions" ,
                              "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                          "<li class=\"function_edit\"><a><span>Edit</span></a></li>" +
                                                         "<li class=\"function_delete\"><a><span>Delete</span></a></li>" +
                                                         "<li class=\"function_send_notification\"><a><span>Send notification</span></a></li>" +
                                                          "</ul></div>"},
                             { "data": "manageUsers",      "sClass": "functions" ,
                                                          "defaultContent": "<div class=\"function_buttons\"><ul>" +
                                                             "<li class=\"function_manage_users\"><a><span>Manage users</span></a></li>" + "</ul></div>"}
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

    var table_groups = $('#table_groups').DataTable(groupTableConfig);

    $('#load_groups').on('click', function() {
        groupTableConfig.ajax = {
          "url": "/api/courses/" + course_id + "/groups",
                      "type": "GET",
                      "dataSrc": "",
        };

        table_groups.destroy();
        table_groups = $('#table_groups').DataTable( groupTableConfig );
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
  var form_group = $('#form_group');
  form_group.validate();

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
    if (param === 'group') {
    $('.lightbox_container.group').show();
    };
    if (param === 'notification') {
        $('.lightbox_container.notification').show();
        };
    if (param === 'users_management') {
        $('.lightbox_container.user_management').show();
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
//#        CREATE group
//# ===============================

  // Add group button
  $(document).on('click', '#add_group', function(e){
    e.preventDefault();
    currentRow = $(this).closest('tr');
    $('.lightbox_content h2').text('Add group');
    $('#form_group button').text('Add group');
    $('#form_group').attr('class', 'form add');
    $('#form_group').attr('data-id', '');
    $('#form_group .field_container label.error').hide();
    $('#form_group .field_container').removeClass('valid').removeClass('error');
    $('#form_group #group_title').val('');
    $('#form_group #users_count').val('');
    show_lightbox('group');
  });

  // Add group submit form
  $(document).on('submit', '#form_group.add', function(e){
    e.preventDefault();
    // Validate form
    if (form_group.valid() == true){
      // Get data from group form
      hide_lightbox('group');
      var group = {};
      const form_data = new FormData(document.querySelector('#form_group'));
      form_data.forEach(function(value, key){
          group[key] = value;
      });
      // Send group information to database
      var request   = $.ajax({
        url:          '/api/courses/' + course_id + '/groups',
        cache:        false,
        data:         JSON.stringify(group),
        contentType: "application/json",
        type:         'POST',
        success: function (id) {
                 show_message("Lesson '" + group.title + "' added successfully.", 'success');
                 group.id = id;
                           table_groups.row.add(group).draw();
                     },
        error: function (e) {
        show_message('Add request failed: '+ JSON.parse(e), 'error');
 }
      });
    }
  });

//# ===============================
//#        EDIT group
//# ===============================

  // Edit group button
  $(document).on('click', '#table_groups .function_edit a', function(e){
    e.preventDefault();
    var id      = $(this).closest('tr').find('.group_id').text();
     currentRow = $(this).closest('tr');
        $('.lightbox_content h2').text('Edit group');
        $('#form_group button').text('Edit group');
        $('#form_group').attr('class', 'form edit');
        $('#form_group').attr('data-id', id);
        $('#form_group .field_container label.error').hide();
        $('#form_group .field_container').removeClass('valid').removeClass('error');
        $('#form_group #group_title').val($('.group_title', currentRow).text());
        $('#form_group #users_count').val($('.group_users_count', currentRow).text());
        show_lightbox('group');
  });
  
  // Edit group submit form
  $(document).on('submit', '#form_group.edit', function(e){
    e.preventDefault();
    // Validate form
    if (form_group.valid() == true){
      // Send group information to server
      hide_lightbox('group');
      var group_id        = $('#form_group').attr('data-id');
      var group = {};
            const form_data = new FormData(document.querySelector('#form_group'));
            form_data.forEach(function(value, key){
                group[key] = value;
            });
            group.id = group_id;

      var request   = $.ajax({
        url:          '/api/courses/' + course_id + '/groups/' + group_id,
        cache:        false,
        data:         JSON.stringify(group),
        contentType: "application/json",
        type:         'PUT',
        success: function (group) {
         show_message("Lesson '" + group.title + "' updated successfully.", 'success');
                                   table_groups.row(currentRow).data(group).draw();
                             },
                error: function (e) {
                show_message('Update request failed: '+ JSON.parse(e), 'error');
         }
      });
    }
  });

  //# ===============================
  //#        DELETE group
  //# ===============================
  
  // Delete group
  $(document).on('click', '#table_groups .function_delete a', function(e){
    e.preventDefault();
    var row = $(this).closest('tr');
    var title =  row.find('.group_title').text();
    if (confirm("Are you sure you want to delete '" + title + "'?")){
      var group_id = row.find('.group_id').text();
      // Send delete request
      var request = $.ajax({
        url:          '/api/courses/' + course_id + '/groups/' + group_id,
        type:         'DELETE',
        success: function () {
        show_message("Lesson '" + title + "' successfully deleted", 'success');
                         table_groups.row(row).remove().draw();
                             },
                error: function (e) {
                show_message('Delete request failed: ' + JSON.parse(e), 'error');
         }
      });
    }
  });


  //# ===============================
  //#        Refresh groups
  //# ===============================

 $(document).on('click', '#refresh_groups', function(e){
 $('#table_groups').DataTable().ajax.reload();
 });


//# ===============================
//#        Update users in the group
//# ===============================

 $(document).on('click', '#table_groups .function_manage_users a', function(e){
 var group_id = $(this).closest('tr').find('.group_id').text();
 var filter_group_list;
 currentRow = $(this).closest('tr');

// Get group users list
 var request = $.ajax({
         url:          '/api/courses/' + course_id + '/groups/' + group_id + '/users',
         type:         'GET',
         async: false,
         success: function (data) {
         $(".content").find("#groupusers").empty();
         filter_group_list = data;
          $.each(data, function(i, f) {
          var new_row = "<option value=\""+ f.id + "\">" + f.login + "</option>";
         $('#groupusers').append(new_row);
       });
      },
           error: function (e) {
           show_message('Failed get users: ' + JSON.parse(e), 'error');
          }
       });

// Get all users list
 $.getJSON('/api/users', function (data) {
                  $(".content").find("#users").empty();
                  var users_list = data;
// Check if there are users in group and remove dublicate from all users list
if (typeof filter_group_list !== 'undefined' && filter_group_list.length > 0) {
     users_list = data.filter(array => filter_group_list.some(filter => filter.id !== array.id));
}
                  $('#user_management').attr('data-id', group_id);
                         $.each(users_list, function(i, f) {
                          var new_row = "<option value=\""+ f.id + "\">" + f.login + "</option>";
                          $('#users').append(new_row);
                       });
                       show_lightbox('users_management');
                          });
});

// Send update list
  $('#send_users').click(function () {
  hide_lightbox('users');
  	var group_id = $('#user_management').attr('data-id');
  			let usersId = [];
      $('#groupusers option').map((index, option) => usersId.push(option.value) );
  var request   = $.ajax({
          url:          '/api/courses/' + course_id + '/groups/' + group_id + "/users",
          cache:        false,
          data:         JSON.stringify(usersId),
          contentType: "application/json",
          type:         'PUT',
          success: function (updatedGroup) {
                   show_message("Users added successfully.", 'success');
                   table_groups.row(currentRow).data(updatedGroup).draw();
                       },
          error: function (e) {
          show_message('Add users request failed: '+ JSON.parse(e), 'error');
   }
        });
  		});

 // Users management buttons

  		$('#add').click(function () {
  			let $options = $('#users option:selected');
  			$options.appendTo("#groupusers");
  		});
  		$('#add_all').click(function () {
  			let $options = $('#users option');
  			$options.appendTo("#groupusers");
  		});
  		$("#remove").click(function () {
  			let $options = $('#groupusers option:selected');
  			$options.appendTo("#users");
  		});
  		$("#remove_all").click(function () {
  			let $options = $('#groupusers option');
  			$options.appendTo('#users');
  		});




 //# ===============================
 //#        SEND NOTIFICATION TO GROUP
 //# ===============================

           // Notification user button
            $(document).on('click', '.function_send_notification a', function(e){
              e.preventDefault();
             var group_id = $(this).closest('tr').find('.group_id').text();
                  $('#form_notification').attr('data-id', group_id);
                  $('#form_notification .field_container label.error').hide();
                  $('#form_notification .field_container').removeClass('valid').removeClass('error');
                  $('#form_notification #notification_subject').val('');
                  $('#form_notification #notification_message').val('');
                  show_lightbox('notification');
            });

            // Create user notification submit form
            $(document).on('submit', '#form_notification', function(e){
              e.preventDefault();
                // Send user information to server
                hide_lightbox();
                var group_id  = $('#form_notification').attr('data-id');
                var notification = {};
                      const form_data = new FormData(document.querySelector('#form_notification'));
                      form_data.forEach(function(value, key){
                          notification[key] = value;
                      });

                var request   = $.ajax({
                  url:          '/api/courses/' + course_id + '/groups/' + group_id + '/notifications',
                  cache:        false,
                  data:         JSON.stringify(notification),
                  contentType: "application/json",
                  type:         'POST',
                  success: function () {
                   show_message("Notification was successfully sent to group with id " + group_id , 'success');
                                             table_users.row(currentRow).data(user).draw();
                                       },
                          error: function (e) {
                          show_message('Notification request failed: '+ JSON.parse(e), 'error');
                   }
                });
            });



        });


