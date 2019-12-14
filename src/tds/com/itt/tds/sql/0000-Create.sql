CREATE DATABASE  IF NOT EXISTS `tds`;
USE `tds`;

/* create client table*/
DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `clientId` int(11) NOT NULL AUTO_INCREMENT,
  `hostName` varchar(45) NOT NULL,
  `userName` varchar(45) NOT NULL,
  PRIMARY KEY (`clientId`),
  UNIQUE KEY `clientId_UNIQUE` (`clientId`),
  CONSTRAINT `client_UNIQUE` UNIQUE (`hostName`,`userName`)
);


/* create task table*/
DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `taskId` int(11) NOT NULL AUTO_INCREMENT,
  `taskName` varchar(45) NOT NULL,
  `taskParameter` json DEFAULT NULL,
  `taskPath` varchar(100) NOT NULL,
  `taskState` enum('PENDING','IN_PROGRESS','COMPLETED') NOT NULL,
  `userId` int(11) NOT NULL,
  `assignedNodeId` int(11) DEFAULT NULL,
  PRIMARY KEY (`taskId`),
  UNIQUE KEY `taskId_UNIQUE` (`taskId`),
  KEY `clientId_idx` (`userId`),
  CONSTRAINT `clientId` FOREIGN KEY (`userId`) REFERENCES `client` (`clientid`) ON DELETE CASCADE ON UPDATE CASCADE
) ;


/* create node table*/
DROP TABLE IF EXISTS `node`;

CREATE TABLE `node` (
  `nodeId` int(11) NOT NULL AUTO_INCREMENT,
  `nodeIp` varchar(255) NOT NULL,
  `nodePort` int(11) NOT NULL,
  `nodeStatus` enum('AVAILABLE','BUSY','NOT_OPERATIONAL') NOT NULL,
  PRIMARY KEY (`nodeId`),
  UNIQUE KEY `nodeId_UNIQUE` (`nodeId`),
  CONSTRAINT `node_UNIQUE` UNIQUE (`nodeIp`,`nodePort`)
);


/* create taskresult table*/
DROP TABLE IF EXISTS `taskresult`;

CREATE TABLE `taskresult` (
  `taskId` int(11) NOT NULL,
  `taskOutcome` enum('SUCCESS','FAILED') NOT NULL,
  `taskErrorCode` int(11) DEFAULT NULL,
  `taskErrorMsg` varchar(100) DEFAULT NULL,
  `taskResultBuffer` mediumblob,
  PRIMARY KEY (`taskId`),
  UNIQUE KEY `taskID_UNIQUE` (`taskId`),
  CONSTRAINT `taskIdKey` FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE CASCADE
) ;


/* create nodetotaskmapping table*/
DROP TABLE IF EXISTS `nodetotaskmapping`;

CREATE TABLE `nodetotaskmapping` (
  `taskId` int(11) NOT NULL,
  `nodeId` int(11) NOT NULL,
  PRIMARY KEY (`taskId`),
  KEY `nodeId_idx` (`nodeId`),
  CONSTRAINT `nodeId` FOREIGN KEY (`nodeId`) REFERENCES `node` (`nodeid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `taskId` FOREIGN KEY (`taskId`) REFERENCES `task` (`taskid`)
) ;
