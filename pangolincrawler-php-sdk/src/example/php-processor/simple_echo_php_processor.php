<?php
require_once __DIR__ . '/../../autoload.php';
use PangolinCrawler\PangolinSdkClient;


$sdkClient = new PangolinSdkClient();


$argArr = $sdkClient->parseCliArgs();

$payload = $argArr['payload'];

$sdkClient->exportCliProcessorResult($payload);