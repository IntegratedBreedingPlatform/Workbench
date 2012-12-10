SELECT MAX(userid) INTO @max_user_id FROM users;
INSERT  INTO `users`(`userid`, `instalid`,`ustatus`,`uaccess`,`utype`,`uname`,`upswd`,`personid`,`adate`,`cdate`) VALUES (IFNULL(@max_user_id, 0) + 1, 0,0,0,0,'workbench','workbench',0,0,0);
