addTestCase({
    name: "wordcount test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T17:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("test-1/input/plain/input/wordcount"), "input/wordcount")
    ],
    outputs: [
        plainCompare(fixDirFromResource("test-1/output/plain/output/wordcount"), "output/wordcount")
    ]
});

addTestCase({
    name: "wordcount test case 2",
    sampleTimeStart: "2013-12-20T18:00Z",
    sampleTimeEnd: "2013-12-20T18:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("test-2/input/plain/input/wordcount"), "input/wordcount")
    ],
    outputs: [
        plainCompare(fixDirFromResource("test-2/output/plain/output/wordcount"), "output/wordcount")
    ]
});