CREATE SCHEMA IF NOT EXISTS `forums` DEFAULT CHARACTER SET latin1 ;
USE `forums` ;

-- -----------------------------------------------------
-- Table `forums`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`user` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) BINARY NOT NULL,
  `timeRegistered` DATETIME NOT NULL,
  `deleted` TINYINT(4) NOT NULL DEFAULT '0',
  `userType` VARCHAR(7) NOT NULL,
  `status` VARCHAR(7) NOT NULL DEFAULT 'FULL',
  `timeBanExit` DATETIME NOT NULL DEFAULT '1900-01-01 00:00:00',
  `banCount` INT(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 579
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `forums`.`forum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`forum` (
  `id` INT(32) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `creator` INT(64) NOT NULL,
  `type` VARCHAR(11) NOT NULL DEFAULT 'UNMODERATED',
  `readOnly` TINYINT(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  INDEX `forum_user_idx` (`creator` ASC),
  CONSTRAINT `forum_user`
    FOREIGN KEY (`creator`)
    REFERENCES `forums`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 132
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `forums`.`message_tree`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`message_tree` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `forumId` INT(32) NOT NULL,
  `subject` VARCHAR(45) NOT NULL,
  `priority` INT(3) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `message_forum_idx` (`forumId` ASC),
  CONSTRAINT `message_forum`
    FOREIGN KEY (`forumId`)
    REFERENCES `forums`.`forum` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 151
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `forums`.`message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`message` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `creatorId` INT(64) NOT NULL,
  `created` DATETIME NOT NULL,
  `parentId` INT(64) NULL DEFAULT NULL,
  `treeId` INT(65) NOT NULL,
  `numberInForum` INT(32) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `message_creator_idx` (`creatorId` ASC) ,
  INDEX `message_parent_idx` (`parentId` ASC) ,
  INDEX `message_tree_idx` (`treeId` ASC) ,
  CONSTRAINT `message_creator`
    FOREIGN KEY (`creatorId`)
    REFERENCES `forums`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `message_parent`
    FOREIGN KEY (`parentId`)
    REFERENCES `forums`.`message` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `message_tree`
    FOREIGN KEY (`treeId`)
    REFERENCES `forums`.`message_tree` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 398
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `forums`.`message_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`message_history` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `messageId` INT(64) NOT NULL,
  `body` VARCHAR(500) CHARACTER SET 'latin1' NOT NULL,
  `state` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `message_body_idx` (`messageId` ASC) ,
  CONSTRAINT `message_body`
    FOREIGN KEY (`messageId`)
    REFERENCES `forums`.`message` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 395
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `forums`.`message_rating`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`message_rating` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `userId` INT(64) NOT NULL,
  `messageId` INT(64) NOT NULL,
  `rating` INT(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UQ_userId_messageId` (`userId` ASC, `messageId` ASC) ,
  INDEX `rating_uer_idx` (`userId` ASC) ,
  INDEX `rating_message_idx` (`messageId` ASC) ,
  CONSTRAINT `rating_message`
    FOREIGN KEY (`messageId`)
    REFERENCES `forums`.`message` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `rating_user`
    FOREIGN KEY (`userId`)
    REFERENCES `forums`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `forums`.`message_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`message_tags` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `treeId` INT(64) NOT NULL,
  `tag` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `tag_message_idx` (`treeId` ASC) ,
  CONSTRAINT `tag_message`
    FOREIGN KEY (`treeId`)
    REFERENCES `forums`.`message_tree` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `forums`.`session`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forums`.`session` (
  `id` INT(64) NOT NULL AUTO_INCREMENT,
  `cookie` VARCHAR(50) NOT NULL,
  `userId` INT(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_session_idx` (`userId` ASC) ,
  CONSTRAINT `user_session`
    FOREIGN KEY (`userId`)
    REFERENCES `forums`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 266
DEFAULT CHARACTER SET = latin1;

INSERT INTO `forums`.`user` (`name`, `email`, `password`, `timeRegistered`, `deleted`, `userType`, `status`, `timeBanExit`, `banCount`) VALUES ('Admin', 'em@em.ru', 'administrator', '2000-01-01', '0', 'SUPER', 'FULL', '1900-01-01', '0');