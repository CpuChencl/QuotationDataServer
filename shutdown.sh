#!/bin/bash
echo "shutdown..."

ps -ef | grep QuotationDataServer | awk '{print $2}' | xargs kill -9
