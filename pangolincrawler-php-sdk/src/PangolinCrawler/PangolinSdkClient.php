<?php

namespace PangolinCrawler;


class PangolinSdkClient
{
    private $config;

    /**
     * PangolinSdkClient constructor.
     * @param $config
     */
    public function __construct($config)
    {
        $this->config = $config;
    }


    public function parseCliArgs()
    {
        global $argv;

        $jsonStr = base64_decode($argv[1]);
        return json_decode($jsonStr, true);
    }

    public function exportCliProcessorResult($result, $exit = true)
    {
        echo $result;
        if ($exit) {
            exit(0);
        }
    }


}