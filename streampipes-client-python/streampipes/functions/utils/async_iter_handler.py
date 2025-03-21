#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import asyncio
from typing import Any, AsyncGenerator, AsyncIterator, Dict, Tuple


class AsyncIterHandler:
    """Handels asyncrone iterators to get every message after an other in parallel."""

    @staticmethod
    async def anext(stream_id: str, message: AsyncIterator) -> Tuple[str, Any]:
        """Gets the next message from an AsncIterator.

        Parameters
        ----------

        stream_id: str
            The id of the data stream which the message belongs to.

        message: AsyncIterator
            An asyncrone iterator that contains the messages.

        Returns
        -------
        Tuple of the stream id und next message or ("stop", None) if no message is left.
        """
        try:
            return stream_id, await message.__anext__()
        except StopAsyncIteration:
            return "stop", None

    @staticmethod
    async def combine_async_messages(messages: Dict[str, AsyncIterator]) -> AsyncGenerator:
        """Continuously gets the next published message from multiple AsncIterators in parallel.

        Parameters
        ----------

        messages: Dict[str, AsyncIterator]
            A dictonary with an asyncrone iterator for every stream id.

        Returns
        -------
        Generator that returns all recieved messages continuously.
        """
        pending = {AsyncIterHandler.anext(stream_id, message) for stream_id, message in messages.items()}
        while pending:
            done, pending = await asyncio.wait(pending, return_when=asyncio.FIRST_COMPLETED)  # type: ignore
            for i in done:
                stream_id, msg = i.result()
                if stream_id != "stop":
                    pending.add(AsyncIterHandler.anext(stream_id, messages[stream_id]))
                yield stream_id, msg
